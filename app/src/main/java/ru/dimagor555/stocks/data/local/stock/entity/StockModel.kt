package ru.dimagor555.stocks.data.local.stock.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StockModel(
    @Embedded
    val stockBaseModel: StockBaseModel,
    @Relation(parentColumn = "priceId", entityColumn = "id")
    val stockPriceModel: StockPriceModel,
)
