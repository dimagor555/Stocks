package ru.dimagor555.stocks.ui;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava2.PagingRx;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kotlinx.coroutines.CoroutineScope;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockRepository;

import javax.inject.Inject;

@HiltViewModel
public class StocksBaseViewModel extends ViewModel {
    protected final StockRepository stockRepository;

    protected final CompositeDisposable disposeBag = new CompositeDisposable();

    protected final MutableLiveData<ErrorModel> errorsLiveData = new MutableLiveData<>();
    protected final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(true);

    @Inject
    public StocksBaseViewModel(StockRepository stockRepository) {
        this.stockRepository = stockRepository;

        disposeBag.add(stockRepository.getNetworkErrorsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> errorsLiveData.setValue(ErrorModel.newErrorModel(e))));
    }

    protected <T> void redirectFlowableToLiveData(Flowable<T> flowable,
                                                  MutableLiveData<T> liveData) {
        redirectFlowableToLiveData(flowable, liveData, disposeBag);
    }

    protected <T> void redirectFlowableToLiveData(Flowable<T> flowable,
                                                  MutableLiveData<T> liveData,
                                                  CompositeDisposable disposable) {
        disposable.add(flowable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(liveData::setValue,
                        this::handleError));
    }

    private void handleError(Throwable throwable) {
        Log.e(getClass().getName(), throwable.getMessage(), throwable);
    }

    protected <T> Flowable<PagingData<T>> cacheInPagingRx(Flowable<PagingData<T>> pagingDataToCache) {
        CoroutineScope coroutineScope = ViewModelKt.getViewModelScope(this);
        return PagingRx.cachedIn(pagingDataToCache, coroutineScope);
    }

    public void updateStockFavourite(String ticker, boolean favourite) {
        stockRepository.updateStockFavourite(ticker, favourite);
    }

    public void notifyStockShownToUser(Stock stock) {
        stockRepository.updateStockFromRemoteIfNeeded(stock);
        onLoadingSuccessfullyEnded();
    }

    protected void onLoadingSuccessfullyEnded() {
        if (loadingLiveData.getValue()) {
            loadingLiveData.postValue(false);
        }
    }

    public MutableLiveData<ErrorModel> getErrors() {
        return errorsLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposeBag.dispose();
        stockRepository.dispose();
    }
}
