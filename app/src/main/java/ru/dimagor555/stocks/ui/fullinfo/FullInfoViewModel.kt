package ru.dimagor555.stocks.ui.fullinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.dimagor555.stocks.data.model.price.Interval
import ru.dimagor555.stocks.data.model.price.Price
import ru.dimagor555.stocks.data.model.price.PriceRepository
import ru.dimagor555.stocks.data.model.stock.StockRepository
import ru.dimagor555.stocks.data.model.stock.entity.Stock
import ru.dimagor555.stocks.ui.StocksBaseViewModel
import javax.inject.Inject

@HiltViewModel
class FullInfoViewModel @Inject constructor(
    stockRepository: StockRepository,
    private val priceRepository: PriceRepository,
    private val lineDataSetFactory: LineDataSetFactory,
) : StocksBaseViewModel(stockRepository) {
    private val currStockLiveData = MutableLiveData<Stock?>(null)
    private val chartDataSetLiveData = MutableLiveData<LineDataSet?>(null)

    private var currInterval: Interval? = null
    private val checkedIntervals = HashSet<Interval>()

    private var pricesDisposable: Disposable? = null

    val currStock
        get() = currStockLiveData as LiveData<Stock?>
    val chartDataSet
        get() = chartDataSetLiveData as LiveData<LineDataSet?>

    fun loadStockData(ticker: String) {
        redirectFlowableToLiveData(
            stockRepository.getStockByTicker(ticker),
            currStockLiveData
        )
    }

    fun loadPricesData(ticker: String, interval: Interval) {
        if (interval == currInterval) return

        val pricesFlowable =
            if (checkedIntervals.contains(interval))
                priceRepository.getPricesByTickerFromTime(ticker, interval, false)
            else priceRepository.getPricesByTickerFromTime(ticker, interval)

        checkedIntervals += interval

        startLoadingPrices()
        redirectPricesFlowableToLiveData(pricesFlowable, interval)
    }

    private fun redirectPricesFlowableToLiveData(
        pricesFlowable: Flowable<List<Price>>,
        interval: Interval
    ) {
        pricesDisposable?.dispose()
        pricesDisposable =
            pricesFlowable
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if (it.isNotEmpty() && it.size > interval.minPricesSize) {
                            val dataSet = lineDataSetFactory.create(it, interval)
                            chartDataSetLiveData.postValue(dataSet)
                        }
                    },
                    this::handleError
                )
    }

    private fun startLoadingPrices() {
        chartDataSetLiveData.value = null
        loadingLiveData.value = true
    }

    fun notifyPricesShownToUser() {
        loadingLiveData.value = false
    }

    override fun notifyStockShownToUser(stock: Stock?) {
        stockRepository.updateStockFromRemoteIfNeeded(stock)
    }
}
