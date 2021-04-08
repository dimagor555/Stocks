package ru.dimagor555.stocks.data.model

import java.text.DecimalFormat
import kotlin.math.abs

object PriceFormatter {
    fun formatPriceInCentsToString(price: Int): String {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(abs(price) / 100.0)
    }
}