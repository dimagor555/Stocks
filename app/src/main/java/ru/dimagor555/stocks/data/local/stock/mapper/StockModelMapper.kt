package ru.dimagor555.stocks.data.local.stock.mapper

import ru.dimagor555.stocks.data.local.stock.entity.StockBaseModel
import ru.dimagor555.stocks.data.local.stock.entity.StockModel
import ru.dimagor555.stocks.data.local.stock.entity.StockPriceModel
import ru.dimagor555.stocks.data.model.stock.entity.Stock
import ru.dimagor555.stocks.data.model.stock.entity.StockCompanyInfo
import javax.inject.Inject

class StockModelMapper @Inject constructor(
    private val priceMapper: StockPriceModelMapper
) {
    fun toStockBaseModel(stock: Stock, priceId: Int): StockBaseModel {
        val companyInfo = stock.companyInfo
        return StockBaseModel(
            stock.ticker,
            companyInfo.companyName,
            companyInfo.companySiteUrl,
            companyInfo.logoUrl,
            stock.isFavourite,
            priceId
        )
    }

    fun toStockPriceModel(stock: Stock, priceId: Int = 0): StockPriceModel {
        return priceMapper.toModel(stock.price, priceId)
    }

    fun fromStockModel(model: StockModel): Stock {
        val companyInfo = with(model.stockBaseModel) {
            StockCompanyInfo(companyName, companySiteUrl, logoUrl)
        }
        val price = priceMapper.fromModel(model.stockPriceModel)
        return Stock(
            model.stockBaseModel.ticker,
            companyInfo,
            price,
            model.stockBaseModel.favourite
        )
    }
}