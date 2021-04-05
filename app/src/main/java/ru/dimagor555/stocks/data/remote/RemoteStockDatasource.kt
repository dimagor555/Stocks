package ru.dimagor555.stocks.data.remote

import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response
import ru.dimagor555.stocks.data.model.stock.Stock
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo
import ru.dimagor555.stocks.data.model.stock.StockPrice
import ru.dimagor555.stocks.data.remote.exception.ApiLimitReachedException
import ru.dimagor555.stocks.data.remote.exception.NetworkErrorException
import ru.dimagor555.stocks.data.remote.exception.UnknownErrorException
import ru.dimagor555.stocks.data.remote.responses.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteStockDatasource @Inject constructor(private val finnhubApi: FinnhubApi) {
    val allStocks: Single<BaseResponse<List<Stock?>?>>
        get() = finnhubApi.stockList
            .subscribeOn(Schedulers.io())
            .flatMap { mapAllStocksResponse(it) }
            .onErrorResumeNext(this::wrapApiCallException)

    private fun mapAllStocksResponse(
        response: Response<StockListResponse?>
    ): Single<BaseResponse<List<Stock?>?>> {
        var stocks: MutableList<Stock?>? = null
        if (response.body() != null) {
            val tickers = response.body()!!.tickers
            stocks = ArrayList()
            tickers.forEach { stocks.add(Stock(it)) }
        }
        return handleResponseErrors(response, stocks)
    }

    fun getStockCompanyInfo(ticker: String?): Single<BaseResponse<StockCompanyInfo>> {
        return finnhubApi.getStockCompanyInfo(ticker)
            .subscribeOn(Schedulers.io())
            .flatMap { mapStockCompanyInfoResponse(it) }
            .onErrorResumeNext(this::wrapApiCallException)
    }

    private fun mapStockCompanyInfoResponse(
        response: Response<StockCompanyInfoResponse>
    ): Single<BaseResponse<StockCompanyInfo>> {
        val data = response.body()
        var companyInfo: StockCompanyInfo? = null
        if (data != null) {
            companyInfo = StockCompanyInfo(
                data.companyName,
                data.companySiteUrl,
                data.logoUrl
            )
        }
        return handleResponseErrors(response, companyInfo)
    }

    fun getStockPrice(ticker: String?): Single<BaseResponse<StockPrice>> {
        return finnhubApi.getStockPrice(ticker)
            .subscribeOn(Schedulers.io())
            .flatMap { mapStockPriceResponse(it) }
            .onErrorResumeNext(this::wrapApiCallException)
    }

    private fun mapStockPriceResponse(
        response: Response<StockPriceResponse>
    ): Single<BaseResponse<StockPrice>> {
        val data = response.body()
        var stockPrice: StockPrice? = null
        if (data != null) {
            stockPrice = StockPrice(
                (data.currPrice * 100).toInt(),
                (data.previousClosePrice * 100).toInt(),
                System.currentTimeMillis()
            )
        }
        return handleResponseErrors(response, stockPrice)
    }

    fun findTickersByTickerOrCompanyName(query: String): Single<BaseResponse<List<String>>> {
        return finnhubApi.findStockByTickerAndCompanyName(query)
            .subscribeOn(Schedulers.io())
            .flatMap { mapFindResponse(it) }
            .onErrorResumeNext(this::wrapApiCallException)
    }

    private fun mapFindResponse(
        response: Response<StockSearchResponse>
    ): Single<BaseResponse<List<String>>> {
        var tickers: List<String>? = null
        if (response.body() != null) {
            tickers = response.body()!!.tickers
        }
        return handleResponseErrors(response, tickers)
    }

    private fun <T> handleResponseErrors(
        response: Response<*>,
        data: T?
    ): Single<BaseResponse<T>> {
        return if (response.isSuccessful) {
            Single.just(BaseResponse(response, data!!))
        } else {
            Single.error(HttpException(response))
        }
    }

    private fun <T> wrapApiCallException(throwable: Throwable): Single<T> {
        return if (throwable is IOException) {
            Single.error(NetworkErrorException())
        } else if (throwable is HttpException) {
            if (throwable.code() == 429) {
                Single.error(ApiLimitReachedException())
            } else {
                Single.error(UnknownErrorException(throwable.message))
            }
        } else {
            Log.e("NETWORK", throwable.message, throwable)
            throw RuntimeException(throwable)
        }
    }
}