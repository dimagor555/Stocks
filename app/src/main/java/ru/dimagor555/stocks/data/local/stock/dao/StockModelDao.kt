package ru.dimagor555.stocks.data.local.stock.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Flowable
import ru.dimagor555.stocks.data.local.stock.entity.StockModel

@Dao
interface StockModelDao {
    @get:Query("select * from stocks order by ticker asc")
    @get:Transaction
    val allStocks: PagingSource<Int, StockModel>

    @get:Query("select * from stocks where favourite = 1 order by ticker asc")
    @get:Transaction
    val favouriteStocks: PagingSource<Int, StockModel>

    @get:Query("select count(*) from stocks where favourite = 1")
    val favouriteCount: Flowable<Int>

    @Transaction
    @Query("select * from stocks where ticker in (:tickers) order by ticker asc")
    fun getStocksByTickers(tickers: List<String>): PagingSource<Int, StockModel>

    @Query(
        "select ticker from stocks " +
                "where " +
                "ticker like :query " +
                "or " +
                "companyName like :query " +
                "order by ticker, companyName asc"
    )
    fun findTickersByTickerOrCompanyName(query: String): List<String>

    @Transaction
    @Query("select * from stocks where ticker = :ticker")
    fun getStockFlowableByTicker(ticker: String): Flowable<StockModel?>
}