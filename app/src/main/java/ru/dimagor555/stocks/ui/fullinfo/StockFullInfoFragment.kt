package ru.dimagor555.stocks.ui.fullinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import ru.dimagor555.stocks.R
import ru.dimagor555.stocks.data.model.stock.Stock

@AndroidEntryPoint
class StockFullInfoFragment : Fragment() {
    private val viewModel by viewModels<FullInfoViewModel>()
    private val args: StockFullInfoFragmentArgs by navArgs()

    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var tvTicker: TextView
    private lateinit var tvCompanyName: TextView
    private lateinit var tvCurrPrice: TextView
    private lateinit var tvDeltaPrice: TextView
    private lateinit var chartPrices: LineChart
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var btnBuy: Button
    private lateinit var btnBack: View
    private lateinit var btnFavourite: View
    private lateinit var ivFavourite: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_full_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViews(view)
        initToggleGroup()
        initHeaderBtns()
        initChart()
        bindViewModel()
        clear()
    }

    private fun findViews(view: View) {
        progressIndicator = view.findViewById(R.id.full_info_frag_progress_indicator)
        tvTicker = view.findViewById(R.id.full_info_frag_text_ticker)
        tvCompanyName = view.findViewById(R.id.full_info_frag_text_company_name)
        tvCurrPrice = view.findViewById(R.id.full_info_frag_text_price)
        tvDeltaPrice = view.findViewById(R.id.full_info_frag_text_price_delta)
        chartPrices = view.findViewById(R.id.full_info_frag_chart_prices)
        toggleGroup = view.findViewById(R.id.full_info_frag_interval_chooser_toggle_group)
        btnBuy = view.findViewById(R.id.full_info_frag_btn_buy)
        btnBack = view.findViewById(R.id.full_info_frag_btn_back_layout)
        btnFavourite = view.findViewById(R.id.full_info_frag_btn_favourite_layout)
        ivFavourite = view.findViewById(R.id.full_info_frag_image_favourite)
    }

    private fun initToggleGroup() {
        toggleGroup.check(R.id.full_info_frag_btn_day)
    }

    private fun initHeaderBtns() {
        btnBack.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

    private fun initChart() {
        chartPrices.data = LineData(listOf())
        chartPrices.description = null
        chartPrices.background = null
        chartPrices.setTouchEnabled(false)

        chartPrices.xAxis.isEnabled = false
        chartPrices.axisLeft.isEnabled = false
        chartPrices.axisRight.isEnabled = false

        chartPrices.legend.isEnabled = false

        chartPrices.setNoDataText("")
    }

    private fun bindViewModel() {
        viewModel.loadStockDate(args.ticker)

        viewModel.currStock.observe(viewLifecycleOwner, {
            if (it != null) bindStock(stock = it)
            else clear()
        })

        viewModel.chartDataSet.observe(viewLifecycleOwner, {
            it?.let {
                chartPrices.data = LineData(it)
                chartPrices.invalidate()
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, {
            it?.let {
                if (it) progressIndicator.show()
                else progressIndicator.hide()
            }
        })

        viewModel.errors.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(
                    context,
                    "${it.getTitle(context)}: ${it.getMessage(context)}",
                    Toast.LENGTH_SHORT
                )
            }
        })
    }

    private fun bindStock(stock: Stock) {
        clear()
        tvTicker.text = stock.ticker
        tvCompanyName.text = stock.companyInfo.companyName
        stock.price.currPrice?.let {
            tvCurrPrice.text = "$${it}"
        }
        with(stock.price) {
            setDeltaPrice(deltaPrice, deltaPricePercent, isDeltaPricePositive)
        }
        setBuyBtnPrice(stock.price.currPrice)
        setFavouriteImage(stock.isFavourite)
        btnFavourite.setOnClickListener {
            stock.toggleFavourite()
            viewModel.updateStockFavourite(stock.ticker, stock.isFavourite)
        }

        viewModel.notifyStockShownToUser(stock)
    }

    private fun setDeltaPrice(
        deltaPrice: String?,
        deltaPricePercent: String?,
        deltaPositive: Boolean
    ) {
        if (deltaPrice != null && deltaPricePercent != null) {
            var toDisplay = if (deltaPositive) "+" else "-"
            toDisplay += "$$deltaPrice (${deltaPricePercent.replace(".", ",")}%)"
            tvDeltaPrice.text = toDisplay
        } else {
            tvDeltaPrice.text = null
        }
        setDeltaPriceColor(deltaPositive)
    }

    private fun setDeltaPriceColor(deltaPricePositive: Boolean) {
        val finalColor =
            if (deltaPricePositive)
                context!!.getColor(R.color.colorGreen)
            else context!!.getColor(R.color.colorRed)

        tvDeltaPrice.setTextColor(finalColor)
    }

    private fun setFavouriteImage(favourite: Boolean) {
        val image =
            if (favourite) context!!.getDrawable(R.drawable.outline_star_24)
            else context!!.getDrawable(R.drawable.outline_star_outline_24)
        ivFavourite.setImageDrawable(image)
    }

    private fun clear() {
        tvTicker.text = null
        tvCompanyName.text = null
        tvCurrPrice.text = null
        tvDeltaPrice.text = null
        setBuyBtnPrice("")
        ivFavourite.setImageDrawable(null)
        btnFavourite.setOnClickListener(null)
    }

    private fun setBuyBtnPrice(price: String?) {
        price?.let {
            btnBuy.text = getString(R.string.buy_for) + " $$price"
        }
    }
}