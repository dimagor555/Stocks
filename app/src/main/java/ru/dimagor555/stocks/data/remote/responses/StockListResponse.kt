package ru.dimagor555.stocks.data.remote.responses

import com.google.gson.annotations.SerializedName

class StockListResponse(@SerializedName("constituents") val tickers: List<String>)