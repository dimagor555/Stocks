package ru.dimagor555.stocks.data.remote;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import ru.dimagor555.stocks.data.remote.responses.StockCompanyInfoResponse;
import ru.dimagor555.stocks.data.remote.responses.StockListResponse;
import ru.dimagor555.stocks.data.remote.responses.StockPriceResponse;

public interface FinnhubApi {
    @Headers("X-Finnhub-Token: c1duanf48v6sjvgft40g")
    @GET("./index/constituents?symbol=^GSPC")
    Single<StockListResponse> getStockList();

    @Headers("X-Finnhub-Token: c1duanf48v6sjvgft40g")
    @GET("./stock/profile2")
    Single<StockCompanyInfoResponse> getStockCompanyInfo(@Query(value = "symbol") String ticker);

    @Headers("X-Finnhub-Token: c1duanf48v6sjvgft40g")
    @GET("./quote")
    Single<StockPriceResponse> getStockPrice(@Query(value = "symbol") String ticker);
}

