package ru.dimagor555.stocks;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ru.dimagor555.stocks.data.local.StocksDatabase;
import ru.dimagor555.stocks.data.local.searchhistory.LocalSearchHistoryRequestDatasource;
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModelDao;
import ru.dimagor555.stocks.data.local.stock.LocalStockDatasource;
import ru.dimagor555.stocks.data.local.stock.StockModelDao;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRepository;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRepositoryImpl;
import ru.dimagor555.stocks.data.model.stock.StockRepository;
import ru.dimagor555.stocks.data.model.stock.StockRepositoryImpl;
import ru.dimagor555.stocks.data.remote.FinnhubApi;
import ru.dimagor555.stocks.data.remote.RemoteStockDatasource;
import ru.dimagor555.stocks.ui.ViewModelFactory;
import ru.dimagor555.stocks.ui.search.SearchViewModel;
import ru.dimagor555.stocks.ui.stocks.StocksViewModel;

public class Injection {
    public static StocksViewModel provideStocksViewModel(Fragment fragment) {
        ViewModelFactory viewModelFactory = provideViewModelFactory(fragment.getContext());
        return new ViewModelProvider(fragment, viewModelFactory).get(StocksViewModel.class);
    }

    public static SearchViewModel provideSearchViewModel(Fragment fragment) {
        ViewModelFactory viewModelFactory = provideViewModelFactory(fragment.getContext());
        return new ViewModelProvider(fragment, viewModelFactory).get(SearchViewModel.class);
    }

    private static ViewModelFactory provideViewModelFactory(Context context) {
        StockRepository stockRepository = getStockRepositoryInstance(context);
        SearchHistoryRepository searchHistoryRepository =
                getSearchHistoryRepositoryInstance(context);
        return new ViewModelFactory(stockRepository, searchHistoryRepository);
    }

    private static volatile StockRepository stockRepositoryInstance;

    private static synchronized StockRepository getStockRepositoryInstance(Context context) {
        if (stockRepositoryInstance == null) {
            StocksDatabase database = StocksDatabase.getInstance(context);
            StockModelDao stockModelDao = database.stockModelDao();
            FinnhubApi finnhubApi = ((StocksApplication) context.getApplicationContext())
                    .getFinnhubApi();

            LocalStockDatasource localStockDatasource = new LocalStockDatasource(stockModelDao);
            RemoteStockDatasource remoteStockDatasource = new RemoteStockDatasource(finnhubApi);

            stockRepositoryInstance = new StockRepositoryImpl(localStockDatasource, remoteStockDatasource);
        }
        return stockRepositoryInstance;
    }

    private static volatile SearchHistoryRepository searchHistoryRepositoryInstance;

    private static synchronized SearchHistoryRepository getSearchHistoryRepositoryInstance(
            Context context) {
        if (searchHistoryRepositoryInstance == null) {
            StocksDatabase database = StocksDatabase.getInstance(context);
            SearchHistoryRequestModelDao dao = database.searchHistoryRequestModelDao();

            LocalSearchHistoryRequestDatasource localDatasource =
                    new LocalSearchHistoryRequestDatasource(dao);

            searchHistoryRepositoryInstance = new SearchHistoryRepositoryImpl(localDatasource);
        }
        return searchHistoryRepositoryInstance;
    }
}
