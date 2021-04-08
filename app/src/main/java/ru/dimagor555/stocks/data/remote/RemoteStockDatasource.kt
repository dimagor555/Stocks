package ru.dimagor555.stocks.data.remote

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import ru.dimagor555.stocks.data.model.stock.entity.Stock
import ru.dimagor555.stocks.data.model.stock.entity.StockCompanyInfo
import ru.dimagor555.stocks.data.model.stock.entity.StockPrice
import ru.dimagor555.stocks.data.remote.RemoteUtils.handleResponseErrors
import ru.dimagor555.stocks.data.remote.responses.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteStockDatasource @Inject constructor(private val finnhubApi: FinnhubApi) {
    val allStocks: Single<BaseResponse<List<Stock?>?>>
        get() = finnhubApi.stockList
            .subscribeOn(Schedulers.io())
            .flatMap { mapAllStocksResponse(it) }
            .onErrorResumeNext(RemoteUtils::wrapApiCallException)

    private fun mapAllStocksResponse(
        response: Response<StockListResponse>
    ): Single<BaseResponse<List<Stock?>?>> {
        var stocks: MutableList<Stock?>? = null
        if (response.body() != null) {
            val tickers = response.body()!!.tickers
            stocks = ArrayList()
            tickers.forEach { stocks.add(Stock(it)) }
        }
        return handleResponseErrors(response, stocks)
    }

    fun getStockCompanyInfo(ticker: String): Single<BaseResponse<StockCompanyInfo>> {
        return finnhubApi.getStockCompanyInfo(ticker)
            .subscribeOn(Schedulers.io())
            .flatMap { mapStockCompanyInfoResponse(it) }
            .onErrorResumeNext(RemoteUtils::wrapApiCallException)
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

    fun getStockPrice(ticker: String): Single<BaseResponse<StockPrice>> {
        return finnhubApi.getStockPrice(ticker)
            .subscribeOn(Schedulers.io())
            .flatMap { mapStockPriceResponse(it) }
            .onErrorResumeNext(RemoteUtils::wrapApiCallException)
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
            .onErrorResumeNext(RemoteUtils::wrapApiCallException)
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
}