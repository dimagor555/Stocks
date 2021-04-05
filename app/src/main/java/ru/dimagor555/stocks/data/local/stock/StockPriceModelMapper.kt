package ru.dimagor555.stocks.data.local.stock

import ru.dimagor555.stocks.data.model.stock.StockPrice
import javax.inject.Inject

class StockPriceModelMapper @Inject constructor() {
    fun toModel(price: StockPrice, priceId: Int) =
        with(price) {
            StockPriceModel(
                id = priceId,
                currPriceInCents = currPriceInCents,
                previousClosePriceInCents = previousClosePriceInCents,
                priceTime = priceTime
            )
        }

    fun fromModel(model: StockPriceModel) =
        with(model) {
            StockPrice(currPriceInCents, previousClosePriceInCents, priceTime)
        }
}