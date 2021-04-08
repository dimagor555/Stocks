package ru.dimagor555.stocks.data.model.price

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

enum class Interval {
    WEEK, MONTH, YEAR;

    fun getFromTime(): Long {
        val currDate = LocalDate.now(Clock.systemUTC())
        val fromDate = when (this) {
            WEEK -> currDate.minusWeeks(1)
            MONTH -> currDate.minusMonths(1)
            YEAR -> currDate.minusYears(1)
        }
        return fromDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
    }
}