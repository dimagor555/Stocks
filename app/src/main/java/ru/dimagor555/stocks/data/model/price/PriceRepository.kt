package ru.dimagor555.stocks.data.model.price

import io.reactivex.Flowable

interface PriceRepository {
    fun getPricesByTickerFromTime(
        ticker: String,
        interval: Interval,
        useRemoteDatasource: Boolean = true,
    ): Flowable<List<Price>>
}