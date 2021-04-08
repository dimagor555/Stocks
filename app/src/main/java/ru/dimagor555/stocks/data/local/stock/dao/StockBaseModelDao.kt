package ru.dimagor555.stocks.data.local.stock.dao

import androidx.room.*
import ru.dimagor555.stocks.data.local.stock.entity.StockBaseModel

@Dao
interface StockBaseModelDao {
    @Query("select exists(select ticker from stocks where ticker = :ticker)")
    fun hasStockWithTicker(ticker: String): Boolean

    @Query("select * from stocks where ticker = :ticker")
    fun getStockByTicker(ticker: String): StockBaseModel?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStockBaseModel(baseModel: StockBaseModel)

    @Update
    fun updateStockBaseModel(baseModel: StockBaseModel)
}