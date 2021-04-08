package ru.dimagor555.stocks.data.remote

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.dimagor555.stocks.data.remote.responses.*

interface FinnhubApi {
    @get:GET("./index/constituents?symbol=^DJI")
    val stockList: Single<Response<StockListResponse>>

    @GET("./stock/profile2")
    fun getStockCompanyInfo(
        @Query(value = "symbol") ticker: String
    ): Single<Response<StockCompanyInfoResponse>>

    @GET("./quote")
    fun getStockPrice(
        @Query(value = "symbol") ticker: String
    ): Single<Response<StockPriceResponse>>

    @GET("./search")
    fun findStockByTickerAndCompanyName(
        @Query(value = "q") request: String
    ): Single<Response<StockSearchResponse>>

    @GET("./stock/candle")
    fun getStockPrices(
        @Query(value = "symbol") ticker: String,
        @Query(value = "resolution") resolution: String,
        @Query(value = "from") from: Long,
        @Query(value = "to") to: Long,
    ): Single<Response<StockPricesResponse>>
}