package ru.dimagor555.stocks.data.model.price

import ru.dimagor555.stocks.data.model.PriceFormatter

data class Price(
    val priceInCents: Int,
    val priceTime: Long,
) {
    val price: String
        get() = PriceFormatter.formatPriceInCentsToString(priceInCents)
}
