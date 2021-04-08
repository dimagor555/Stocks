package ru.dimagor555.stocks.data.model.price

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

enum class Interval {
    WEEK, MONTH, YEAR;

    val fromTime: Long
        get() {
            val currDate = LocalDate.now(Clock.systemUTC())
            val fromDate = when (this) {
                WEEK -> currDate.minusWeeks(1)
                MONTH -> currDate.minusMonths(1)
                YEAR -> currDate.minusYears(1)
            }
            return fromDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        }

    val minPricesSize: Int
        get() =
            when (this) {
                WEEK -> 1
                MONTH -> 8
                YEAR -> 32
            }
}