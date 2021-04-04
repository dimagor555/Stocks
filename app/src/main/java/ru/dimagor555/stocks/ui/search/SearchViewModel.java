package ru.dimagor555.stocks.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingData;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRepository;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRequest;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockRepository;
import ru.dimagor555.stocks.data.model.stock.StockSearcher;
import ru.dimagor555.stocks.ui.StocksBaseViewModel;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

@HiltViewModel
public class SearchViewModel extends StocksBaseViewModel {
    private final SearchHistoryRepository searchHistoryRepository;
    private final StockSearcher stockSearcher;

    private final MutableLiveData<PagingData<Stock>> searchResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> nothingFoundLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> emptySearchLiveData = new MutableLiveData<>(true);

    private final MutableLiveData<List<SearchHistoryRequest>> searchHistoryLiveDate =
            new MutableLiveData<>();
    private final MutableLiveData<String> searchTextLiveData = new MutableLiveData<>(null);

    private final BehaviorSubject<String> searchRequestsSubject = BehaviorSubject.create();
    private final CompositeDisposable searchRequestsDisposable = new CompositeDisposable();

    @Inject
    public SearchViewModel(StockRepository stockRepository,
                           SearchHistoryRepository searchHistoryRepository, StockSearcher stockSearcher) {
        super(stockRepository);
        this.searchHistoryRepository = searchHistoryRepository;
        this.stockSearcher = stockSearcher;

        loadingLiveData.setValue(false);

        redirectFlowableToLiveData(searchHistoryRepository.getAllRequests(),
                searchHistoryLiveDate);

        disposeBag.add(searchRequestsSubject
                .subscribeOn(Schedulers.io())
                .debounce(800, TimeUnit.MILLISECONDS)
                .subscribe(this::makeNewSearchRequest));
    }

    public void onSearchFieldUpdated(String request) {
        searchRequestsSubject.onNext(request);
    }

    private void makeNewSearchRequest(String request) {
        searchRequestsDisposable.clear();
        searchResultLiveData.postValue(PagingData.empty());
        if (!request.isEmpty()) {
            showSearchResultScreen();
            addSearchRequestToHistory(request);
            startLoading(request);
        } else {
            showStartSearchScreen();
        }
    }

    private void startLoading(String request) {
        showLoadingInProgress();
        searchRequestsDisposable.add(stockSearcher
                .findTickersByTickerOrCompanyName(request)
                .subscribeOn(Schedulers.io())
                .subscribe(tickers -> {
                    if (!tickers.isEmpty()) {
                        loadSearchResult(tickers);
                    } else {
                        showLoadingEndedNothingFound();
                    }
                }));
    }

    private void loadSearchResult(List<String> tickers) {
        Flowable<PagingData<Stock>> searchResult =
                cacheInPagingRx(stockRepository.getStocksByTickers(tickers));

        redirectFlowableToLiveData(
                searchResult,
                searchResultLiveData,
                searchRequestsDisposable);
    }

    private void addSearchRequestToHistory(String request) {
        searchHistoryRepository.addRequest(new SearchHistoryRequest(request,
                System.currentTimeMillis()));
    }

    private void showLoadingInProgress() {
        loadingLiveData.postValue(true);
        nothingFoundLiveData.postValue(false);
    }

    private void showLoadingEndedWithResult() {
        loadingLiveData.postValue(false);
        nothingFoundLiveData.postValue(false);
    }

    private void showLoadingEndedNothingFound() {
        loadingLiveData.postValue(false);
        nothingFoundLiveData.postValue(true);
    }

    private void showStartSearchScreen() {
        emptySearchLiveData.postValue(true);
        showLoadingEndedWithResult();
    }

    private void showSearchResultScreen() {
        emptySearchLiveData.postValue(false);
    }

    @Override
    protected void onLoadingSuccessfullyEnded() {
        showLoadingEndedWithResult();
    }

    public void onSelectSearchRequestFromHistory(SearchHistoryRequest request) {
        searchTextLiveData.postValue(request.getRequestText());
    }

    public boolean onDeleteSearchRequestFromHistory(SearchHistoryRequest request) {
        searchHistoryRepository.deleteRequest(request);
        return true;
    }

    public LiveData<PagingData<Stock>> getSearchResult() {
        return searchResultLiveData;
    }

    public LiveData<Boolean> getNothingFound() {
        return nothingFoundLiveData;
    }

    public LiveData<Boolean> getEmptySearch() {
        return emptySearchLiveData;
    }

    public LiveData<List<SearchHistoryRequest>> getSearchHistory() {
        return searchHistoryLiveDate;
    }

    public MutableLiveData<String> getSearchText() {
        return searchTextLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        searchRequestsDisposable.dispose();
    }
}
