package ru.dimagor555.stocks.data.local.price

import ru.dimagor555.stocks.data.model.price.Price
import javax.inject.Inject

class PriceModelMapper @Inject constructor() {
    fun toModel(price: Price, ticker: String) =
        with(price) {
            PriceModel(
                ticker = ticker,
                priceInCents = priceInCents,
                priceTime = priceTime
            )
        }

    fun fromModel(priceModel: PriceModel) =
        with(priceModel) {
            Price(priceInCents, priceTime)
        }
}