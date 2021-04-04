package ru.dimagor555.stocks.data.remote.responses

import com.google.gson.annotations.SerializedName

class StockSearchResponse(
    @SerializedName("result")
    private val tickersSearchResult: List<SearchResultStock>,
) {
    val tickers
        get() = tickersSearchResult
            .map { it.toString() }
            .filter { it.contains(Regex("^[A-Z]{1,4}\$")) }

    class SearchResultStock(@SerializedName("symbol") val ticker: String) {
        override fun toString(): String {
            return ticker
        }
    }
}