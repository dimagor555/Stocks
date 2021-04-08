package ru.dimagor555.stocks.data.local.stock.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockBaseModel(
    @PrimaryKey val ticker: String,
    val companyName: String?,
    val companySiteUrl: String?,
    val logoUrl: String?,
    val favourite: Boolean,
    val priceId: Int,
)