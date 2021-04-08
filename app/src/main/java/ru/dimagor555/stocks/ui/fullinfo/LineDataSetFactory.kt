package ru.dimagor555.stocks.ui.fullinfo

import android.graphics.Color
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import ru.dimagor555.stocks.data.model.price.Interval
import ru.dimagor555.stocks.data.model.price.Price
import javax.inject.Inject

class LineDataSetFactory @Inject constructor() {
    fun create(prices: List<Price>, interval: Interval): LineDataSet {
        val values = prices.map {
            Entry(it.priceTime.toFloat(), it.priceInCents.toFloat())
        }

        val dataSet = LineDataSet(values, null)
        dataSet.setDrawValues(false)
        dataSet.setDrawIcons(false)
        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawHighlightIndicators(false)
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.color = Color.BLACK
        dataSet.circleColors = listOf(Color.BLACK)
        dataSet.circleRadius = 3f
        dataSet.lineWidth = 3f

        if (interval == Interval.YEAR)
            dataSet.setDrawCircles(false)

        return dataSet
    }
}