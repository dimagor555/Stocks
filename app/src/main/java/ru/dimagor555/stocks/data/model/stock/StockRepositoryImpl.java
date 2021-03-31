package ru.dimagor555.stocks.data.model.stock;

import androidx.paging.PagingData;
import io.reactivex.Flowable;
import ru.dimagor555.stocks.data.local.stock.LocalStockDatasource;
import ru.dimagor555.stocks.data.remote.RemoteRequest;
import ru.dimagor555.stocks.data.remote.RemoteRequestQueue;
import ru.dimagor555.stocks.data.remote.RemoteStockDatasource;

public class StockRepositoryImpl implements StockRepository {
    private final LocalStockDatasource localRepository;
    private final RemoteRequestQueue remoteRequestQueue;

    public StockRepositoryImpl(LocalStockDatasource localRepository,
                               RemoteStockDatasource remoteStockDatasource) {
        this.localRepository = localRepository;
        remoteRequestQueue = new RemoteRequestQueue(remoteStockDatasource, localRepository);
    }

    @Override
    public Flowable<PagingData<Stock>> getAllStocks() {
        loadAllStocksFromRemoteRepository();
        return localRepository.getAllStocks();
    }

    private void loadAllStocksFromRemoteRepository() {
        remoteRequestQueue.addRequest(new RemoteRequest.AllStocks());
    }

    @Override
    public Flowable<PagingData<Stock>> getFavouriteStocks() {
        return localRepository.getFavouriteStocks();
    }

    @Override
    public Flowable<Boolean> isFavouriteListEmpty() {
        return localRepository.isFavouriteListEmpty();
    }

    @Override
    public Flowable<PagingData<Stock>> findByTickerAndCompanyName(String request) {
        return localRepository.findByTickerAndCompanyName(request);
    }

    @Override
    public boolean hasSearchResultByTickerAndCompanyName(String request) {
        return localRepository.hasSearchResultByTickerAndCompanyName(request);
    }

    @Override
    public void updateStockFromRemoteIfNeeded(Stock stock) {
        if (stock.getCompanyInfo().isEmpty()) {
            remoteRequestQueue.addRequest(new RemoteRequest.CompanyInfo(stock.getTicker()));
        }
        if (!stock.getPrice().isFresh()) {
            remoteRequestQueue.addRequest(new RemoteRequest.Price(stock.getTicker()));
        }
    }

    @Override
    public void insertStock(Stock stock) {
         localRepository.insertStock(stock);
    }

    @Override
    public void updateStock(Stock stock) {
        localRepository.updateStock(stock);
    }

    @Override
    public Flowable<Exception> getNetworkErrorsObservable() {
        return remoteRequestQueue.getNetworkErrorsObservable();
    }

    @Override
    public void dispose() {
        remoteRequestQueue.dispose();
    }
}
