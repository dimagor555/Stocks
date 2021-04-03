package ru.dimagor555.stocks.data.remote.requests

sealed class RemoteRequest(private val priority: RequestPriority) : Comparable<RemoteRequest> {
    var isInProgress = false
    var isFinished = false
        set(value) {
            field = value
            isInProgress = false
        }

    override fun compareTo(other: RemoteRequest): Int {
        return priority.compareTo(other.priority)
    }

    class AllStocks : RemoteRequest(RequestPriority.ALL_STOCKS)

    data class Search(val query: String) : RemoteRequest(RequestPriority.SEARCH)

    data class CompanyInfo(val ticker: String) : RemoteRequest(RequestPriority.COMPANY_INFO)

    data class Price(val ticker: String) : RemoteRequest(RequestPriority.PRICE)

    enum class RequestPriority {
        ALL_STOCKS, SEARCH, COMPANY_INFO, PRICE
    }
}