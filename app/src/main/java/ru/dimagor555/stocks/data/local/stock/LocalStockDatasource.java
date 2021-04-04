package ru.dimagor555.stocks.data.local.stock;

import android.util.Log;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingSource;
import androidx.paging.rxjava2.PagingRx;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import kotlin.jvm.functions.Function0;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo;
import ru.dimagor555.stocks.data.model.stock.StockPrice;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class LocalStockDatasource {
    private final StockModelDao dao;
    private final StockModelMapper mapper;

    @Inject
    public LocalStockDatasource(StockModelDao dao, StockModelMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    public Flowable<PagingData<Stock>> getAllStocks() {
        return createFlowableStockPagingDataFromPagingSource(dao::getAllStocks);
    }

    public Flowable<PagingData<Stock>> getFavouriteStocks() {
        return createFlowableStockPagingDataFromPagingSource(dao::getFavouriteStocks);
    }

    public Flowable<Boolean> isFavouriteListEmpty() {
        return dao.getFavouriteCount().map(count -> count == 0);
    }

    public Flowable<PagingData<Stock>> getStocksByTickers(List<String> tickers) {
        return createFlowableStockPagingDataFromPagingSource(() ->
                dao.getStocksByTickers(tickers));
    }

    private Flowable<PagingData<Stock>> createFlowableStockPagingDataFromPagingSource(
            Function0<PagingSource<Integer, StockModel>> pagingSource) {
        Pager<Integer, StockModel> pager = new Pager<>(
                new PagingConfig(20, 5, false),
                pagingSource);
        return PagingRx.getFlowable(pager).flatMap(this::pagingDataFromModel);
    }

    private Flowable<PagingData<Stock>> pagingDataFromModel(
            PagingData<StockModel> stockModelPagingData) {
        return Flowable
                .just(PagingRx.map(stockModelPagingData, stockModel ->
                        Single.just(mapper.fromModel(stockModel))));
    }

    public List<String> findTickersByTickerOrCompanyName(String request) {
        return dao.findTickersByTickerOrCompanyName("%" + request + "%");
    }

    public void insertStock(Stock stock) {
        dao.insertStock(mapper.toModel(stock))
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Log.e("DB", throwable.getMessage(), throwable))
                .subscribe();
    }

    public void updateStock(Stock stock) {
        dao.updateStock(mapper.toModel(stock))
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Log.e("DB", throwable.getMessage(), throwable))
                .subscribe();
    }

    public void updateStockCompanyInfo(String ticker, StockCompanyInfo newCompanyInfo) {
        Stock updatableStock = getStockByTicker(ticker);
        updatableStock.getCompanyInfo()
                .setCompanyInfo(
                        newCompanyInfo.getCompanyName(),
                        newCompanyInfo.getCompanySiteUrl(),
                        newCompanyInfo.getLogoUrl());
        updateStock(updatableStock);
    }

    public void updateStockPriceInfo(String ticker, StockPrice newPrice) {
        Stock updatableStock = getStockByTicker(ticker);
        updatableStock.getPrice()
                .setPrice(
                        newPrice.getCurrPriceInCents(),
                        newPrice.getPreviousClosePriceInCents(),
                        newPrice.getPriceTime());
        updateStock(updatableStock);
    }

    public Stock getStockByTicker(String ticker) {
        return mapper.fromModel(dao.getStockByTicker(ticker));
    }
}
