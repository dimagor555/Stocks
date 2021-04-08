package ru.dimagor555.stocks.data.model.stock;

import androidx.paging.PagingData;
import io.reactivex.Flowable;
import ru.dimagor555.stocks.data.local.stock.LocalStockDatasource;
import ru.dimagor555.stocks.data.model.stock.entity.Stock;
import ru.dimagor555.stocks.data.remote.requests.RemoteRequest;
import ru.dimagor555.stocks.data.remote.requests.RemoteRequestManager;

import javax.inject.Inject;
import java.util.List;

public class StockRepositoryImpl implements StockRepository {
    private final LocalStockDatasource localRepository;
    private final RemoteRequestManager remoteRequestManager;

    @Inject
    public StockRepositoryImpl(LocalStockDatasource localRepository,
                               RemoteRequestManager remoteRequestManager) {
        this.localRepository = localRepository;
        this.remoteRequestManager = remoteRequestManager;
    }

    @Override
    public Flowable<PagingData<Stock>> getAllStocks() {
        loadAllStocksFromRemoteRepository();
        return localRepository.getAllStocks();
    }

    private void loadAllStocksFromRemoteRepository() {
        remoteRequestManager.addRequest(new RemoteRequest.AllStocks());
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
    public Flowable<PagingData<Stock>> getStocksByTickers(List<String> tickers) {
        return localRepository.getStocksByTickers(tickers);
    }

    @Override
    public Flowable<Stock> getStockByTicker(String ticker) {
        return localRepository.getStockByTicker(ticker);
    }

    @Override
    public void updateStockFromRemoteIfNeeded(Stock stock) {
        if (stock.getCompanyInfo().isEmpty()) {
            remoteRequestManager.addRequest(new RemoteRequest.CompanyInfo(stock.getTicker()));
        }
        if (!stock.getPrice().isFresh()) {
            remoteRequestManager.addRequest(new RemoteRequest.Price(stock.getTicker()));
        }
    }

    @Override
    public void updateStockFavourite(String ticker, boolean favourite) {
        localRepository.updateStockFavourite(ticker, favourite);
    }

    @Override
    public Flowable<Exception> getNetworkErrorsObservable() {
        return remoteRequestManager.getNetworkErrorsObservable();
    }
}
