package ru.dimagor555.stocks.data.local.stock

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.rxjava2.flowable
import androidx.paging.rxjava2.mapAsync
import io.reactivex.Flowable
import io.reactivex.Single
import ru.dimagor555.stocks.data.local.DbUtils
import ru.dimagor555.stocks.data.local.StocksDatabase
import ru.dimagor555.stocks.data.local.stock.dao.StockBaseModelDao
import ru.dimagor555.stocks.data.local.stock.dao.StockModelDao
import ru.dimagor555.stocks.data.local.stock.dao.StockPriceModelDao
import ru.dimagor555.stocks.data.local.stock.entity.StockModel
import ru.dimagor555.stocks.data.local.stock.mapper.StockModelMapper
import ru.dimagor555.stocks.data.model.stock.entity.Stock
import ru.dimagor555.stocks.data.model.stock.entity.StockCompanyInfo
import ru.dimagor555.stocks.data.model.stock.entity.StockPrice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStockDatasource @Inject constructor(
    private val database: StocksDatabase,
    private val stockDao: StockModelDao,
    private val stockBaseDao: StockBaseModelDao,
    private val stockPriceDao: StockPriceModelDao,
    private val mapper: StockModelMapper
) {
    val allStocks: Flowable<PagingData<Stock>>
        get() = createFlowableStockPagingDataFromPagingSource { stockDao.allStocks }
    val favouriteStocks: Flowable<PagingData<Stock>>
        get() = createFlowableStockPagingDataFromPagingSource { stockDao.favouriteStocks }
    val isFavouriteListEmpty: Flowable<Boolean>
        get() = stockDao.favouriteCount.map { count: Int -> count == 0 }

    fun getStocksByTickers(tickers: List<String>): Flowable<PagingData<Stock>> {
        return createFlowableStockPagingDataFromPagingSource { stockDao.getStocksByTickers(tickers) }
    }

    fun getStockByTicker(ticker: String): Flowable<Stock?> {
        val stockModelFlowable = stockDao.getStockFlowableByTicker(ticker = ticker)
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
        return stockDao.findTickersByTickerOrCompanyName("%$request%")
    }

    fun insertStock(stock: Stock) {
        DbUtils.runDbQuery {
            if (stockBaseDao.hasStockWithTicker(stock.ticker))
                return@runDbQuery

            database.runInTransaction {
                val priceModelToInsert = mapper.toStockPriceModel(stock)
                val priceId = stockPriceDao
                    .insertStockPriceModel(priceModelToInsert).toInt()
                val stockBaseModel = mapper.toStockBaseModel(stock, priceId)
                stockBaseDao.insertStockBaseModel(stockBaseModel)
            }
        }
    }

    fun updateStockFavourite(ticker: String, favourite: Boolean) {
        DbUtils.runDbQuery {
            stockBaseDao.getStockByTicker(ticker)?.let {
                val baseModelToUpdate = it.copy(favourite = favourite)
                stockBaseDao.updateStockBaseModel(baseModelToUpdate)
            }
        }
    }

    fun updateStockCompanyInfo(ticker: String, newCompanyInfo: StockCompanyInfo) {
        DbUtils.runDbQuery {
            stockBaseDao.getStockByTicker(ticker)?.let {
                val baseModelToUpdate =
                    with(newCompanyInfo) {
                        it.copy(
                            companyName = companyName,
                            companySiteUrl = companySiteUrl,
                            logoUrl = logoUrl
                        )
                    }
                stockBaseDao.updateStockBaseModel(baseModelToUpdate)
            }
        }
    }

    fun updateStockPriceInfo(ticker: String, newPrice: StockPrice) {
        DbUtils.runDbQuery {
            stockBaseDao.getStockByTicker(ticker)?.let {
                val oldPrice = stockPriceDao.getStockPriceById(it.priceId)
                val priceModelToUpdate =
                    with(newPrice) {
                        oldPrice.copy(
                            currPriceInCents = currPriceInCents,
                            previousClosePriceInCents = previousClosePriceInCents,
                            priceTime = priceTime
                        )
                    }
                stockPriceDao.updateStockPriceModel(priceModelToUpdate)
            }
        }
    }
}
