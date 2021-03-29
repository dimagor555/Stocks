package ru.dimagor555.stocks.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagingData;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRepository;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRequest;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockRepository;
import ru.dimagor555.stocks.ui.StocksBaseViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchViewModel extends StocksBaseViewModel {
    private final SearchHistoryRepository searchHistoryRepository;

    private final MutableLiveData<PagingData<Stock>> searchResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> nothingFoundLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> emptySearchLiveData = new MutableLiveData<>(true);
    private final MutableLiveData<List<SearchHistoryRequest>> searchHistoryLiveDate =
            new MutableLiveData<>();
    private final MutableLiveData<String> searchTextLiveData = new MutableLiveData<>(null);

    private final BehaviorSubject<String> searchRequestsSubject = BehaviorSubject.create();
    private final CompositeDisposable searchRequestsDisposable = new CompositeDisposable();

    public SearchViewModel(StockRepository stockRepository,
                           SearchHistoryRepository searchHistoryRepository) {
        super(stockRepository);
        this.searchHistoryRepository = searchHistoryRepository;

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
            emptySearchLiveData.postValue(false);
            searchHistoryRepository.addRequest(new SearchHistoryRequest(request,
                    System.currentTimeMillis()));
            if (stockRepository.hasSearchResultByTickerAndCompanyName(request)) {
                loadSearchResult(request);
            } else {
                showLoadingNothingFound();
            }
        } else {
            searchResultLiveData.postValue(PagingData.empty());
            emptySearchLiveData.postValue(true);
            showLoadingEndedSuccessfully();
        }
    }

    private void loadSearchResult(String request) {
        Flowable<PagingData<Stock>> searchResult =
                cacheInPagingRx(stockRepository.searchByTickerAndCompanyName(request));

        redirectFlowableToLiveData(
                searchResult,
                searchResultLiveData,
                searchRequestsDisposable);

        showLoading();
    }

    private void showLoading() {
        loadingLiveData.postValue(true);
        nothingFoundLiveData.postValue(false);
    }

    private void showLoadingEndedSuccessfully() {
        loadingLiveData.postValue(false);
        nothingFoundLiveData.postValue(false);
    }

    private void showLoadingNothingFound() {
        loadingLiveData.postValue(false);
        nothingFoundLiveData.postValue(true);
    }

    @Override
    protected void onLoadingSuccessfullyEnded() {
        showLoadingEndedSuccessfully();
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
