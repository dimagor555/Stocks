package ru.dimagor555.stocks.data.remote;

import android.util.Log;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo;
import ru.dimagor555.stocks.data.model.stock.StockPrice;
import ru.dimagor555.stocks.data.remote.exception.ApiLimitReachedException;
import ru.dimagor555.stocks.data.remote.exception.NetworkErrorException;
import ru.dimagor555.stocks.data.remote.exception.UnknownErrorException;
import ru.dimagor555.stocks.data.remote.responses.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RemoteStockDatasource {
    private final FinnhubApi finnhubApi;

    @Inject
    public RemoteStockDatasource(FinnhubApi finnhubApi) {
        this.finnhubApi = finnhubApi;
    }

    public Single<BaseResponse<List<Stock>>> getAllStocks() {
        return finnhubApi.getStockList()
                .subscribeOn(Schedulers.io())
                .flatMap(this::mapAllStocksResponse)
                .onErrorResumeNext(this::wrapApiCallException);
    }

    private Single<BaseResponse<List<Stock>>> mapAllStocksResponse(
            Response<StockListResponse> response) {
        List<Stock> stocks = null;
        if (response.body() != null) {
            List<String> tickers = response.body().getTickers();
            stocks = new ArrayList<>();
            List<Stock> finalStocks = stocks;

            tickers.forEach(s -> finalStocks.add(new Stock(s)));

            for (String s : tickers) {
                finalStocks.add(new Stock(s));
            }
        }
        return handleResponseErrors(response, stocks);
    }

    public Single<BaseResponse<StockCompanyInfo>> getStockCompanyInfo(String ticker) {
        return finnhubApi.getStockCompanyInfo(ticker)
                .subscribeOn(Schedulers.io())
                .flatMap(this::mapStockCompanyInfoResponse)
                .onErrorResumeNext(this::wrapApiCallException);
    }

    private Single<BaseResponse<StockCompanyInfo>> mapStockCompanyInfoResponse(
            Response<StockCompanyInfoResponse> response) {
        StockCompanyInfoResponse data = response.body();
        StockCompanyInfo companyInfo = null;
        if (data != null) {
            companyInfo = new StockCompanyInfo(
                    data.getCompanyName(),
                    data.getCompanySiteUrl(),
                    data.getLogoUrl());
        }
        return handleResponseErrors(response, companyInfo);
    }

    public Single<BaseResponse<StockPrice>> getStockPrice(String ticker) {
        return finnhubApi.getStockPrice(ticker)
                .subscribeOn(Schedulers.io())
                .flatMap(this::mapStockPriceResponse)
                .onErrorResumeNext(this::wrapApiCallException);
    }

    private Single<BaseResponse<StockPrice>> mapStockPriceResponse(
            Response<StockPriceResponse> response) {
        StockPriceResponse data = response.body();
        StockPrice stockPrice = null;
        if (data != null) {
            stockPrice = new StockPrice(
                    (int) (data.getCurrPrice() * 100),
                    (int) (data.getPreviousClosePrice() * 100),
                    System.currentTimeMillis());
        }
        return handleResponseErrors(response, stockPrice);
    }

    public Single<BaseResponse<List<String>>> findStockByTickerAndCompanyName(String query) {
        return finnhubApi.findStockByTickerAndCompanyName(query)
                .subscribeOn(Schedulers.io())
                .flatMap(this::mapFindResponse)
                .onErrorResumeNext(this::wrapApiCallException);
    }

    private Single<BaseResponse<List<String>>> mapFindResponse(
            Response<StockSearchResponse> response) {
        List<String> tickers = null;
        if (response.body() != null) {
            tickers = response.body().getTickers();
        }
        return handleResponseErrors(response, tickers);
    }

    private <T> Single<BaseResponse<T>> handleResponseErrors(
            Response<?> response,
            T data
    ) {
        if (response.isSuccessful()) {
            return Single.just(new BaseResponse<>(response, data));
        } else {
            return Single.error(new HttpException(response));
        }
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
            Log.e("NETWORK", throwable.getMessage(), throwable);
            throw new RuntimeException(throwable);
        }
    }
}
