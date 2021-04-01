package ru.dimagor555.stocks.data.local.stock;

import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo;
import ru.dimagor555.stocks.data.model.stock.StockPrice;

import javax.inject.Inject;

public class StockModelMapper {
    @Inject
    public StockModelMapper() {
    }

    public StockModel toModel(Stock stock) {
        StockCompanyInfo companyInfo = stock.getCompanyInfo();
        StockPrice price = stock.getPrice();
        return new StockModel(stock.getTicker(),
                companyInfo.getCompanyName(),
                companyInfo.getCompanySiteUrl(),
                companyInfo.getLogoUrl(),
                price.getCurrPriceInCents(),
                price.getPreviousClosePriceInCents(),
                price.getPriceTime(),
                stock.isFavourite());
    }

    public Stock fromModel(StockModel model) {
        return new Stock(model.getTicker(),
                model.getCompanyName(),
                model.getCompanySiteUrl(),
                model.getLogoUrl(),
                model.getCurrPriceInCents(),
                model.getPreviousClosePriceInCents(),
                model.getPriceTime(),
                model.isFavourite());
    }
}
