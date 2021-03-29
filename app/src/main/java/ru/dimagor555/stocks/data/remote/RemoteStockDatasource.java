package ru.dimagor555.stocks.data.remote;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo;
import ru.dimagor555.stocks.data.model.stock.StockPrice;
import ru.dimagor555.stocks.data.remote.exception.ApiLimitReachedException;
import ru.dimagor555.stocks.data.remote.exception.NetworkErrorException;
import ru.dimagor555.stocks.data.remote.exception.UnknownErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteStockDatasource {
    private final FinnhubApi finnhubApi;

    public RemoteStockDatasource(FinnhubApi finnhubApi) {
        this.finnhubApi = finnhubApi;
    }

    public Single<List<Stock>> getAllStocks() {
        return finnhubApi.getStockList()
                .subscribeOn(Schedulers.io())
                .flatMap(it -> {
                    List<String> tickers = it.getTickers();
                    List<Stock> stocks = new ArrayList<>(tickers.size());
                    tickers.forEach(s -> stocks.add(new Stock(s)));
                    return Single.just(stocks);
                })
                .onErrorResumeNext(this::wrapApiCallException);
    }

    public Single<StockCompanyInfo> getStockCompanyInfo(String ticker) {
        return finnhubApi.getStockCompanyInfo(ticker)
                .subscribeOn(Schedulers.io())
                .flatMap(it ->
                        Single.just(new StockCompanyInfo(
                                it.getCompanyName(),
                                it.getCompanySiteUrl(),
                                it.getLogoUrl())))
                .onErrorResumeNext(this::wrapApiCallException);
    }

    public Single<StockPrice> getStockPrice(String ticker) {
        return finnhubApi.getStockPrice(ticker)
                .subscribeOn(Schedulers.io())
                .flatMap(it ->
                        Single.just(new StockPrice(
                                (int) (it.getCurrPrice() * 100),
                                (int) (it.getPreviousClosePrice() * 100),
                                System.currentTimeMillis())))
                .onErrorResumeNext(this::wrapApiCallException);
    }

    private Single wrapApiCallException(Throwable throwable) {
        if (throwable instanceof IOException) {
            return Single.error(new NetworkErrorException());
        } else if (throwable instanceof HttpException) {
            HttpException httpException = ((HttpException) throwable);
            if (httpException.code() == 429) {
                return Single.error(new ApiLimitReachedException());
            } else {
                return Single.error(new UnknownErrorException(httpException.getMessage()));
            }
        } else {
            return Single.error(new UnknownErrorException(throwable.getMessage()));
        }
    }
}
