package ru.dimagor555.stocks.data.remote;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import ru.dimagor555.stocks.data.local.stock.LocalStockDatasource;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo;
import ru.dimagor555.stocks.data.model.stock.StockPrice;
import ru.dimagor555.stocks.data.remote.exception.ApiLimitReachedException;
import ru.dimagor555.stocks.data.remote.exception.NetworkErrorException;
import ru.dimagor555.stocks.data.remote.exception.UnknownErrorException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This class is wrapper for remote datasource. It controls api limit and order of request.
 * Automatically updates fetched data to local db
 */
@Singleton
public class RemoteRequestQueue {
    //Api limit is 60 calls per minute
    private final static int MAX_CONCURRENT_REQUESTS = 10;
    private final static int API_LIMIT_RECOVERY_TIME_IN_SECONDS = 60;

    private final RemoteStockDatasource remoteStockDatasource;
    private final LocalStockDatasource localStockDatasource;

    private final List<RemoteRequest> requestQueue = new CopyOnWriteArrayList<>();
    private volatile boolean apiLimitReached = false;

    private final CompositeDisposable disposeBag = new CompositeDisposable();
    private final PublishSubject<Exception> networkErrorsObservable = PublishSubject.create();

    @Inject
    public RemoteRequestQueue(RemoteStockDatasource remoteStockDatasource,
                              LocalStockDatasource localStockDatasource) {
        this.remoteStockDatasource = remoteStockDatasource;
        this.localStockDatasource = localStockDatasource;
    }

    public void addRequest(RemoteRequest request) {
        if (!requestQueue.contains(request)) {
            requestQueue.add(request);
            requestQueue.sort(RemoteRequest::compareTo);
            notifyMayBePendingRequests();
        }
    }

    private void notifyMayBePendingRequests() {
        if (!requestQueue.isEmpty() && canMakeNewRequest()) {
            for (RemoteRequest request : requestQueue) {
                if (canMakeNewRequest() && !request.isInProgress()) {
                    makeRequest(request);
                }
            }
        }
    }

    private synchronized boolean canMakeNewRequest() {
        return !apiLimitReached && requestsInProgressCount() <= MAX_CONCURRENT_REQUESTS;
    }

    private synchronized int requestsInProgressCount() {
        int result = 0;
        for (RemoteRequest request : requestQueue) {
            if (request.isInProgress()) {
                result++;
            }
        }
        return result;
    }

    private void makeRequest(RemoteRequest request) {
        request.setInProgress(true);
        Disposable requestDisposable = null;
        if (request instanceof RemoteRequest.AllStocks) {
            requestDisposable = buildDefaultRequest(
                    remoteStockDatasource.getAllStocks(), request)
                    .subscribe(this::handleAllStocksResult,
                            this::handleRemoteError);
        } else if (request instanceof RemoteRequest.CompanyInfo) {
            RemoteRequest.CompanyInfo companyInfoRequest = ((RemoteRequest.CompanyInfo) request);
            String ticker = companyInfoRequest.getTicker();
            requestDisposable = buildDefaultRequest(
                    remoteStockDatasource.getStockCompanyInfo(ticker), request)
                    .subscribe(companyInfo ->
                                    handleCompanyInfoResult(ticker, companyInfo),
                            this::handleRemoteError);
        } else if (request instanceof RemoteRequest.Price) {
            RemoteRequest.Price priceRequest = ((RemoteRequest.Price) request);
            String ticker = priceRequest.getTicker();
            requestDisposable = buildDefaultRequest(
                    remoteStockDatasource.getStockPrice(ticker), request)
                    .subscribe(price -> handlePriceResult(ticker, price),
                            this::handleRemoteError);
        }
        disposeBag.add(requestDisposable);
    }

    private <T> Single<T> buildDefaultRequest(Single<T> request, RemoteRequest requestInfo) {
        return request
                .subscribeOn(Schedulers.io())
                .doAfterSuccess(t -> requestQueue.remove(requestInfo))
                .doOnError(throwable -> requestInfo.setInProgress(false))
                .doAfterTerminate(this::notifyMayBePendingRequests);
    }

    private void handleAllStocksResult(List<Stock> stocks) {
        for (Stock stock : stocks) {
            localStockDatasource.insertStock(stock);
        }
    }

    private void handleCompanyInfoResult(String ticker, StockCompanyInfo companyInfo) {
        localStockDatasource.updateStockCompanyInfo(ticker, companyInfo);
    }

    private void handlePriceResult(String ticker, StockPrice price) {
        localStockDatasource.updateStockPriceInfo(ticker, price);
    }

    private void handleRemoteError(Throwable error) {
        if (error instanceof ApiLimitReachedException || error instanceof NetworkErrorException) {
            suspendRequestToApi();
            networkErrorsObservable.onNext((Exception) error);
        } else if (error instanceof UnknownErrorException) {
            networkErrorsObservable.onNext((Exception) error);
        }
    }

    private void suspendRequestToApi() {
        apiLimitReached = true;
        disposeBag.add(Completable.complete()
                .subscribeOn(Schedulers.computation())
                .delay(API_LIMIT_RECOVERY_TIME_IN_SECONDS, TimeUnit.SECONDS)
                .subscribe(this::continueRequestToApi));
    }

    private void continueRequestToApi() {
        apiLimitReached = false;
        notifyMayBePendingRequests();
    }

    public Flowable<Exception> getNetworkErrorsObservable() {
        return networkErrorsObservable.toFlowable(BackpressureStrategy.LATEST);
    }

    public void dispose() {
        disposeBag.dispose();
    }
}
