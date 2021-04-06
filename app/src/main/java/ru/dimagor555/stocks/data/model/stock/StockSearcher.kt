package ru.dimagor555.stocks.data.model.stock

import android.annotation.SuppressLint
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import ru.dimagor555.stocks.data.local.stock.LocalStockDatasource
import ru.dimagor555.stocks.data.remote.requests.RemoteRequest
import ru.dimagor555.stocks.data.remote.requests.RemoteRequestManager
import java.util.*
import javax.inject.Inject

typealias Tickers = List<String>

class StockSearcher @Inject constructor(
    private val localStockDatasource: LocalStockDatasource,
    private val remoteRequestManager: RemoteRequestManager,
) {
    @SuppressLint("CheckResult")
    fun findTickersByTickerOrCompanyName(request: String): Single<Tickers> {
        val resultSingleSubject = SingleSubject.create<Tickers>()
        val localTickers = localStockDatasource.findTickersByTickerOrCompanyName(request)

        remoteRequestManager.makeSearchRequest(RemoteRequest.Search(request))
            .subscribeOn(Schedulers.io())
            .subscribe({ handleSearchResult(localTickers, it, resultSingleSubject) },
                { handleSearchResult(localTickers, emptyTickers(), resultSingleSubject) })

        return resultSingleSubject
    }

    private fun handleSearchResult(
        localTickers: Tickers,
        remoteTickers: Tickers,
        resultSingleSubject: SingleSubject<Tickers>
    ) {
        val totalTickers = LinkedList<String>()
        totalTickers.addAll(localTickers)
        totalTickers.addAll(remoteTickers)
        resultSingleSubject.onSuccess(totalTickers)
    }

    private fun emptyTickers() = LinkedList<String>()
}
