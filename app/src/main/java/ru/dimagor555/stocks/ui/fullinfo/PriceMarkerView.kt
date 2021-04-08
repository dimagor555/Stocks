package ru.dimagor555.stocks.ui.fullinfo

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ru.dimagor555.stocks.R
import ru.dimagor555.stocks.data.model.PriceFormatter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class PriceMarkerView(
    context: Context?
) : MarkerView(context, R.layout.chart_price_marker_view) {
    private val tvPrice = findViewById<TextView>(R.id.price_marker_text_price)
    private val tvDate = findViewById<TextView>(R.id.price_marker_text_date)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val priceInCents = e.y.toInt()
            val priceToDisplay = PriceFormatter.formatPriceInCentsToString(priceInCents)
            tvPrice.text = "$$priceToDisplay"

            val priceTime = e.x.toLong()
            val dateFormat = SimpleDateFormat("dd MMMM yyyy")
            val date = LocalDateTime.ofEpochSecond(priceTime, 0, ZoneOffset.UTC)
            tvDate.text = dateFormat.format(Date.from(date.toInstant(ZoneOffset.UTC)))
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -1.2f * height.toFloat())
    }
}