package ru.dimagor555.stocks.data.model.stock

import java.text.DecimalFormat
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate

data class StockPrice constructor(
    private var _currPriceInCents: Int = 0,
    private var _previousClosePriceInCents: Int = 0,
    private var _priceTime: Long = 0,
) {
    //Storing price in cents for solving float point issue with money
    val currPriceInCents
        get() = _currPriceInCents
    val previousClosePriceInCents
        get() = _previousClosePriceInCents
    private var deltaPriceInCents = 0

    val priceTime
        get() = _priceTime

    init {
        updateDeltaPrice()
    }

    fun setPrice(currPriceInCents: Int, previousClosePriceInCents: Int, priceTime: Long) {
        _currPriceInCents = currPriceInCents
        _previousClosePriceInCents = previousClosePriceInCents
        _priceTime = priceTime
        updateDeltaPrice()
    }

    private fun updateDeltaPrice() {
        deltaPriceInCents = _currPriceInCents - _previousClosePriceInCents
    }

    val currPrice: String?
        get() = if (!isEmpty) {
            priceInCentsToString(_currPriceInCents)
        } else {
            null
        }
    val deltaPrice: String?
        get() = if (!isEmpty) {
            priceInCentsToString(deltaPriceInCents)
        } else {
            null
        }
    val deltaPricePercent: String?
        get() = if (!isEmpty) {
            val decimalFormat = DecimalFormat("#.##")
            decimalFormat.format(
                (Math.abs(deltaPriceInCents)
                        / (_previousClosePriceInCents / 100f)).toDouble()
            )
        } else {
            null
        }

    private fun priceInCentsToString(price: Int): String {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(Math.abs(price) / 100.0)
    }

    val isDeltaPricePositive: Boolean
        get() = deltaPriceInCents >= 0
    val isFresh: Boolean
        get() = (isPriceTimeFresh || isNowWeekend) && !isEmpty
    private val isPriceTimeFresh: Boolean
        get() = (System.currentTimeMillis() - _priceTime) / 1000 / 60 <
                PRICE_EXPIRE_INTERVAL_IN_MINUTES

    //no trading & price info at the weekend
    private val isNowWeekend: Boolean
        get() {
            val nowDayOfWeek = LocalDate.now(Clock.systemUTC()).dayOfWeek
            return nowDayOfWeek == DayOfWeek.SUNDAY || nowDayOfWeek == DayOfWeek.SATURDAY
        }
    private val isEmpty: Boolean
        get() = _currPriceInCents == 0 && deltaPriceInCents == 0

    companion object {
        private const val PRICE_EXPIRE_INTERVAL_IN_MINUTES: Long = 30
    }
}