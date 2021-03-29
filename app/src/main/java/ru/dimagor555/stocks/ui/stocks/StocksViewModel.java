package ru.dimagor555.stocks.ui.stocks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingData;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockRepository;
import ru.dimagor555.stocks.ui.StocksBaseViewModel;

public class StocksViewModel extends StocksBaseViewModel {
    private final MutableLiveData<PagingData<Stock>> allStocksLiveData = new MutableLiveData<>();
    private final MutableLiveData<PagingData<Stock>> favouriteStocksLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> favouriteListEmptyLiveData = new MutableLiveData<>();

    public StocksViewModel(StockRepository stockRepository) {
        super(stockRepository);

        redirectFlowableToLiveData(
                cacheInPagingRx(stockRepository.getAllStocks()), allStocksLiveData);

        redirectFlowableToLiveData(
                cacheInPagingRx(stockRepository.getFavouriteStocks()), favouriteStocksLiveData);

        redirectFlowableToLiveData(stockRepository.isFavouriteListEmpty(),
                favouriteListEmptyLiveData);
    }

    public LiveData<PagingData<Stock>> getAllStocks() {
        return allStocksLiveData;
    }

    public LiveData<PagingData<Stock>> getFavouriteStocks() {
        return favouriteStocksLiveData;
    }

    public LiveData<Boolean> getFavouriteListEmpty() {
        return favouriteListEmptyLiveData;
    }
}
