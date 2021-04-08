package ru.dimagor555.stocks.data.local.stock.dao

import androidx.room.*
import ru.dimagor555.stocks.data.local.stock.entity.StockPriceModel

@Dao
interface StockPriceModelDao {
    @Query("select * from stock_prices where id = :id")
    fun getStockPriceById(id: Int): StockPriceModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStockPriceModel(priceModel: StockPriceModel): Long

    @Update
    fun updateStockPriceModel(priceModel: StockPriceModel)
}