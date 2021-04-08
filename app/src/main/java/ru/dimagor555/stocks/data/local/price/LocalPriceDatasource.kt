package ru.dimagor555.stocks.data.local.price

import ru.dimagor555.stocks.data.local.DbUtils
import ru.dimagor555.stocks.data.model.price.Price
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalPriceDatasource @Inject constructor(
    private val priceDao: PriceModelDao,
    private val mapper: PriceModelMapper,
) {
    fun getPricesByTickerFromTime(ticker: String, from: Long) =
        priceDao.getPricesByTickerFromTime(ticker, from)
            .map { it.map { mapper.fromModel(it) } }

    fun insertPrices(stockPrices: List<Price>, ticker: String) = DbUtils.runDbQuery {
        priceDao.insertPriceModels(stockPrices.map { mapper.toModel(it, ticker) })
    }
}