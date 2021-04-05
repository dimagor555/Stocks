package ru.dimagor555.stocks.ui.fullinfo

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.dimagor555.stocks.data.model.stock.Stock
import ru.dimagor555.stocks.data.model.stock.StockRepository
import ru.dimagor555.stocks.ui.StocksBaseViewModel
import javax.inject.Inject

@HiltViewModel
class FullInfoViewModel @Inject constructor(
    stockRepository: StockRepository
) : StocksBaseViewModel(stockRepository) {
    private val currStockLiveData = MutableLiveData<Stock?>(null)
    private val chartDataSetLiveData = MutableLiveData<LineDataSet?>(null)

    val currStock
        get() = currStockLiveData as LiveData<Stock?>
    val chartDataSet
        get() = chartDataSetLiveData as LiveData<LineDataSet?>

    fun loadStockDate(ticker: String) {
        redirectFlowableToLiveData(
            stockRepository.getStockByTicker(ticker),
            currStockLiveData
        )

        val values = listOf(Entry(1f, 50f), Entry(2f, 600f))
        val dataSet = LineDataSet(values, null)
        dataSet.setDrawValues(false)
        dataSet.setDrawIcons(false)
        dataSet.setDrawCircles(false)
        dataSet.color = Color.BLACK
        dataSet.lineWidth = 2f

        chartDataSetLiveData.value = dataSet
    }
}
