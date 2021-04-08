package ru.dimagor555.stocks.data.model.price

import io.reactivex.Flowable
import ru.dimagor555.stocks.data.local.price.LocalPriceDatasource
import ru.dimagor555.stocks.data.remote.requests.RemoteRequest
import ru.dimagor555.stocks.data.remote.requests.RemoteRequestManager
import javax.inject.Inject

class PriceRepositoryImpl @Inject constructor(
    private val localDatasource: LocalPriceDatasource,
    private val remoteRequestManager: RemoteRequestManager,
) : PriceRepository {
    override fun getPricesByTickerFromTime(
        ticker: String, interval: Interval,
        useRemoteDatasource: Boolean
    ): Flowable<List<Price>> {
        val from = interval.getFromTime()
        if (useRemoteDatasource)
            remoteRequestManager.addRequest(RemoteRequest.Prices(ticker, from))
        return localDatasource.getPricesByTickerFromTime(ticker, from)
    }
}