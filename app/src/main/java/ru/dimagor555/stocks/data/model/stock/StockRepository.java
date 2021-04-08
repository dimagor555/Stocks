package ru.dimagor555.stocks.data.model.stock;

import androidx.paging.PagingData;
import io.reactivex.Flowable;
import ru.dimagor555.stocks.data.model.stock.entity.Stock;

import java.util.List;

public interface StockRepository {
    Flowable<PagingData<Stock>> getAllStocks();

    Flowable<PagingData<Stock>> getFavouriteStocks();

    Flowable<Boolean> isFavouriteListEmpty();

    Flowable<PagingData<Stock>> getStocksByTickers(List<String> tickers);

    Flowable<Stock> getStockByTicker(String ticker);

    void updateStockFromRemoteIfNeeded(Stock stock);

    void updateStockFavourite(String ticker, boolean favourite);

    Flowable<Exception> getNetworkErrorsObservable();
}
