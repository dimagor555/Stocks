package ru.dimagor555.stocks.data.model.stock

import android.annotation.SuppressLint
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import ru.dimagor555.stocks.data.local.stock.LocalStockDatasource
import ru.dimagor555.stocks.data.remote.RemoteStockDatasource
import java.util.*
import javax.inject.Inject

typealias Tickers = List<String>

class StockSearcher @Inject constructor(
    private val localStockDatasource: LocalStockDatasource,
    private val remoteStockDatasource: RemoteStockDatasource,
) {
    @SuppressLint("CheckResult")
    fun findTickersByTickerOrCompanyName(request: String): Single<Tickers> {
        val resultSingleSubject = SingleSubject.create<Tickers>()
        val localTickers = localStockDatasource.findTickersByTickerOrCompanyName(request)

        remoteStockDatasource.findTickersByTickerOrCompanyName(request)
            .subscribeOn(Schedulers.io())
            .subscribe({ handleSearchResult(localTickers, it.data, resultSingleSubject) },
                { handleSearchResult(localTickers, emptyTickers(), resultSingleSubject) })

        return resultSingleSubject
    }

    private fun handleSearchResult(
        localTickers: Tickers,
        remoteTickers: Tickers,
        resultSingleSubject: SingleSubject<Tickers>
    ) {
        addRemoteTickersToLocalDb(remoteTickers)

        val totalTickers = LinkedList<String>()
        totalTickers.addAll(localTickers)
        totalTickers.addAll(remoteTickers)
        resultSingleSubject.onSuccess(totalTickers)
    }

    private fun addRemoteTickersToLocalDb(remoteTickers: Tickers) {
        remoteTickers.forEach {
            localStockDatasource.insertStock(Stock(it))
        }
    }

    private fun emptyTickers() = LinkedList<String>()
}
