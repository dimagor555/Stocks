package ru.dimagor555.stocks.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModel;
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModelDao;
import ru.dimagor555.stocks.data.local.stock.StockBaseModel;
import ru.dimagor555.stocks.data.local.stock.StockModelDao;
import ru.dimagor555.stocks.data.local.stock.StockPriceModel;

@Database(entities = {StockBaseModel.class, StockPriceModel.class,
        SearchHistoryRequestModel.class},
        version = 7, exportSchema = false)
public abstract class StocksDatabase extends RoomDatabase {
    public static final String DB_NAME = "stocks.db";

    public abstract StockModelDao stockModelDao();

    public abstract SearchHistoryRequestModelDao searchHistoryRequestModelDao();

}
