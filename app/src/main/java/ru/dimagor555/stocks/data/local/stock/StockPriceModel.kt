package ru.dimagor555.stocks.data.local.stock

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prices")
data class StockPriceModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //Storing price in cents for solving float point issue with money
    val currPriceInCents: Int,
    val previousClosePriceInCents: Int,
    val priceTime: Long,
)


