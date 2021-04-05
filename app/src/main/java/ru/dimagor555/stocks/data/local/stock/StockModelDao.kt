package ru.dimagor555.stocks.data.local.stock

import androidx.paging.PagingSource
import androidx.room.*
import io.reactivex.Flowable

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStockBaseModel(baseModel: StockBaseModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStockPriceModel(priceModel: StockPriceModel): Long

    @Update
    fun updateStockBaseModel(baseModel: StockBaseModel)

    @Update
    fun updateStockPriceModel(priceModel: StockPriceModel)

    @Transaction
    @Query("select * from stocks where ticker = :ticker")
    fun getStockByTicker(ticker: String): StockModel?

    @Query("select exists(select ticker from stocks where ticker = :ticker)")
    fun hasStockWithTicker(ticker: String): Boolean
}