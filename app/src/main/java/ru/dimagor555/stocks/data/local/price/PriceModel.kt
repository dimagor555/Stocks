package ru.dimagor555.stocks.data.local.price

import androidx.room.Entity

@Entity(tableName = "prices", primaryKeys = ["ticker", "priceTime"])
data class PriceModel(
    val ticker: String,
    //Storing price in cents for solving float point issue with money
    val priceInCents: Int,
    val priceTime: Long,
)