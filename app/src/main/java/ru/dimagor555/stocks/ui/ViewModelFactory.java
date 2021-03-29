package ru.dimagor555.stocks.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import org.jetbrains.annotations.NotNull;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRepository;
import ru.dimagor555.stocks.data.model.stock.StockRepository;
import ru.dimagor555.stocks.ui.search.SearchViewModel;
import ru.dimagor555.stocks.ui.stocks.StocksViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final StockRepository stockRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    public ViewModelFactory(StockRepository stockRepository,
                            SearchHistoryRepository searchHistoryRepository) {
        this.stockRepository = stockRepository;
        this.searchHistoryRepository = searchHistoryRepository;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StocksViewModel.class)) {
            return (T) new StocksViewModel(stockRepository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(stockRepository, searchHistoryRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
