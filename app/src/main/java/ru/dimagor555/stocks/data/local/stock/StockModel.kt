package ru.dimagor555.stocks.data.local.stock

import androidx.room.Embedded
import androidx.room.Relation

data class StockModel(
    @Embedded
    val stockBaseModel: StockBaseModel,
    @Relation(parentColumn = "priceId", entityColumn = "id")
    val stockPriceModel: StockPriceModel,
)
