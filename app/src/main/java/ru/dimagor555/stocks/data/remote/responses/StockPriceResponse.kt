package ru.dimagor555.stocks.data.remote.responses

import com.google.gson.annotations.SerializedName

class StockPriceResponse(
    @SerializedName("c") val currPrice: Float,
    @SerializedName("pc") val previousClosePrice: Float
)