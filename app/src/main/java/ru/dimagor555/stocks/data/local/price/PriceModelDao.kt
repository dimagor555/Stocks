package ru.dimagor555.stocks.data.local.price

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface PriceModelDao {
    @Query("select * from prices where ticker = :ticker and priceTime >= :from")
    fun getPricesByTickerFromTime(ticker: String, from: Long): Flowable<List<PriceModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPriceModels(priceModels: List<PriceModel>)
}