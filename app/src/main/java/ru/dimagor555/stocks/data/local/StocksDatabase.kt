package ru.dimagor555.stocks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.dimagor555.stocks.data.local.price.PriceModel
import ru.dimagor555.stocks.data.local.price.PriceModelDao
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModel
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModelDao
import ru.dimagor555.stocks.data.local.stock.dao.StockBaseModelDao
import ru.dimagor555.stocks.data.local.stock.dao.StockModelDao
import ru.dimagor555.stocks.data.local.stock.dao.StockPriceModelDao
import ru.dimagor555.stocks.data.local.stock.entity.StockBaseModel
import ru.dimagor555.stocks.data.local.stock.entity.StockPriceModel

@Database(
    entities = [
        StockBaseModel::class,
        StockPriceModel::class,
        PriceModel::class,
        SearchHistoryRequestModel::class,
    ],
    version = 10,
    exportSchema = false
)
abstract class StocksDatabase : RoomDatabase() {
    abstract fun stockModelDao(): StockModelDao
    abstract fun stockBaseModelDao(): StockBaseModelDao
    abstract fun stockPriceModelDao(): StockPriceModelDao
    abstract fun priceModelDao(): PriceModelDao
    abstract fun searchHistoryRequestModelDao(): SearchHistoryRequestModelDao

    companion object {
        const val DB_NAME = "stocks.db"
    }
}