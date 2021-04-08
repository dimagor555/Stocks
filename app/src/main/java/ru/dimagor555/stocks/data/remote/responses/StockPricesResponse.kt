package ru.dimagor555.stocks.data.remote.responses

import com.google.gson.annotations.SerializedName

class StockPricesResponse(
    @SerializedName("c") val prices: Array<Float>,
    @SerializedName("t") val times: Array<Long>,
)