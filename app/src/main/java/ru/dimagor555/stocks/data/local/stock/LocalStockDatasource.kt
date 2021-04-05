package ru.dimagor555.stocks.data.local.stock

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.rxjava2.flowable
import androidx.paging.rxjava2.mapAsync
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import ru.dimagor555.stocks.data.model.stock.Stock
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo
import ru.dimagor555.stocks.data.model.stock.StockPrice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStockDatasource @Inject constructor(
    private val dao: StockModelDao,
    private val mapper: StockModelMapper
) {
    val allStocks: Flowable<PagingData<Stock>>
        get() = createFlowableStockPagingDataFromPagingSource { dao.allStocks }
    val favouriteStocks: Flowable<PagingData<Stock>>
        get() = createFlowableStockPagingDataFromPagingSource { dao.favouriteStocks }
    val isFavouriteListEmpty: Flowable<Boolean>
        get() = dao.favouriteCount.map { count: Int -> count == 0 }

    fun getStocksByTickers(tickers: List<String>): Flowable<PagingData<Stock>> {
        return createFlowableStockPagingDataFromPagingSource { dao.getStocksByTickers(tickers) }
    }

    fun getStockByTicker(ticker: String): Flowable<Stock?> {
        val stockModelFlowable = dao.getStockFlowableByTicker(ticker = ticker)
        return stockModelFlowable.map {
            return@map mapper.fromStockModel(it)
        }
    }

    private fun createFlowableStockPagingDataFromPagingSource(
        pagingSource: Function0<PagingSource<Int, StockModel>>
    ): Flowable<PagingData<Stock>> {
        val pager = Pager(
            PagingConfig(20, 5, false),
            null,
            pagingSource
        )
        return pager.flowable.flatMap { pagingDataFromModel(it) }
    }

    private fun pagingDataFromModel(
        stockModelPagingData: PagingData<StockModel>
    ): Flowable<PagingData<Stock>> {
        return Flowable
            .just(stockModelPagingData.mapAsync {
                Single.just(mapper.fromStockModel(it))
            })
    }

    fun findTickersByTickerOrCompanyName(request: String): List<String> {
        return dao.findTickersByTickerOrCompanyName("%$request%")
    }

    fun insertStock(stock: Stock) {
        runDbQuery {
            if (dao.hasStockWithTicker(stock.ticker))
                return@runDbQuery

            val priceModelToInsert = mapper.toStockPriceModel(stock)
            val priceId = dao.insertStockPriceModel(priceModelToInsert).toInt()
            val stockBaseModel = mapper.toStockBaseModel(stock, priceId)
            dao.insertStockBaseModel(stockBaseModel)
        }
    }

    fun updateStockFavourite(ticker: String, favourite: Boolean) {
        runDbQuery {
            dao.getStockByTicker(ticker)?.let {
                val baseModelToUpdate = it.stockBaseModel.copy(favourite = favourite)
                dao.updateStockBaseModel(baseModelToUpdate)
            }
        }
    }

    fun updateStockCompanyInfo(ticker: String, newCompanyInfo: StockCompanyInfo) {
        runDbQuery {
            dao.getStockByTicker(ticker)?.let {
                val baseModelToUpdate =
                    with(newCompanyInfo) {
                        it.stockBaseModel.copy(
                            companyName = companyName,
                            companySiteUrl = companySiteUrl,
                            logoUrl = logoUrl
                        )
                    }
                dao.updateStockBaseModel(baseModelToUpdate)
            }
        }
    }

    fun updateStockPriceInfo(ticker: String, newPrice: StockPrice) {
        runDbQuery {
            dao.getStockByTicker(ticker)?.let {
                val priceModelToUpdate =
                    with(newPrice) {
                        it.stockPriceModel.copy(
                            currPriceInCents = currPriceInCents,
                            previousClosePriceInCents = previousClosePriceInCents,
                            priceTime = priceTime
                        )
                    }
                dao.updateStockPriceModel(priceModelToUpdate)
            }
        }
    }

    private fun runDbQuery(action: Action) {
        Completable.fromAction(action)
            .subscribeOn(Schedulers.io())
            .doOnError { logDbError(it) }
            .subscribe()
    }

    private fun logDbError(error: Throwable) {
        Log.e("DB", error.message, error)
    }
}
