package ru.dimagor555.stocks.data.remote;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.dimagor555.stocks.data.remote.responses.StockCompanyInfoResponse;
import ru.dimagor555.stocks.data.remote.responses.StockListResponse;
import ru.dimagor555.stocks.data.remote.responses.StockPriceResponse;
import ru.dimagor555.stocks.data.remote.responses.StockSearchResponse;

public interface FinnhubApi {
    @GET("./index/constituents?symbol=^DJI")
    Single<Response<StockListResponse>> getStockList();

    @GET("./stock/profile2")
    Single<Response<StockCompanyInfoResponse>> getStockCompanyInfo(@Query(value = "symbol") String ticker);

    @GET("./quote")
    Single<Response<StockPriceResponse>> getStockPrice(@Query(value = "symbol") String ticker);

    @GET("./search")
    Single<Response<StockSearchResponse>> findStockByTickerAndCompanyName(@Query(value = "q") String request);
}

