package ru.dimagor555.stocks.data.model.stock;

import androidx.paging.PagingData;
import io.reactivex.Flowable;

import java.util.List;

public interface StockRepository {
    Flowable<PagingData<Stock>> getAllStocks();

    Flowable<PagingData<Stock>> getFavouriteStocks();

    Flowable<Boolean> isFavouriteListEmpty();

    Flowable<PagingData<Stock>> getStocksByTickers(List<String> tickers);

    void updateStockFromRemoteIfNeeded(Stock stock);

    void insertStock(Stock stock);

    void updateStock(Stock stock);

    Flowable<Exception> getNetworkErrorsObservable();

    void dispose();
}
