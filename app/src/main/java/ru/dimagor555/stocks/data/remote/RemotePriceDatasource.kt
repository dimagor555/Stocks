package ru.dimagor555.stocks.data.remote

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import ru.dimagor555.stocks.data.model.price.Price
import ru.dimagor555.stocks.data.remote.responses.BaseResponse
import ru.dimagor555.stocks.data.remote.responses.StockPricesResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemotePriceDatasource @Inject constructor(private val finnhubApi: FinnhubApi) {
    fun getPrices(ticker: String, from: Long): Single<BaseResponse<List<Price>>> =
        finnhubApi.getStockPrices(ticker, DAY_RESOLUTION, from, currTime())
            .subscribeOn(Schedulers.io())
            .flatMap { mapPricesResponse(it) }
            .onErrorResumeNext(RemoteUtils::wrapApiCallException)

    private fun mapPricesResponse(
        response: Response<StockPricesResponse>
    ): Single<BaseResponse<List<Price>>> {
        val data = response.body()
        var prices: MutableList<Price>? = null
        data?.let {
            prices = ArrayList()
            for (i in it.prices.indices) {
                prices as ArrayList<Price> +=
                    Price((it.prices[i] * 100).toInt(), it.times[i])
            }
        }
        return RemoteUtils.handleResponseErrors(response, prices)
    }

    private fun currTime(): Long = System.currentTimeMillis() / 1000

    companion object {
        private const val DAY_RESOLUTION = "D"
    }
}