package ru.dimagor555.stocks.data.local.stock;

import androidx.paging.PagingSource;
import androidx.room.*;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface StockModelDao {
    @Query("select * from stocks order by ticker asc")
    PagingSource<Integer, StockModel> getAllStocks();

    @Query("select * from stocks where favourite = 1 order by ticker asc")
    PagingSource<Integer, StockModel> getFavouriteStocks();

    @Query("select count(*) from stocks where favourite = 1")
    Flowable<Integer> getFavouriteCount();

    @Query("select * from stocks " +
            "where " +
            "ticker like :request " +
            "or " +
            "companyName like :request " +
            "order by ticker, companyName asc")
    PagingSource<Integer, StockModel> findByTickerAndCompanyName(String request);

    @Query("select count(*) from stocks " +
            "where " +
            "ticker like :request " +
            "or " +
            "companyName like :request ")
    Integer countOfStocksFoundByTickerAndCompanyName(String request);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertStock(StockModel stock);

    @Update
    Completable updateStock(StockModel stock);

    @Query("select * from stocks where ticker = :ticker")
    StockModel getStockByTicker(String ticker);
}
