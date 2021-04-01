package ru.dimagor555.stocks.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModel;
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModelDao;
import ru.dimagor555.stocks.data.local.stock.StockModel;
import ru.dimagor555.stocks.data.local.stock.StockModelDao;

@Database(entities = {StockModel.class, SearchHistoryRequestModel.class},
        version = 5, exportSchema = false)
public abstract class StocksDatabase extends RoomDatabase {
    public static final String DB_NAME = "stocks.db";

    public abstract StockModelDao stockModelDao();

    public abstract SearchHistoryRequestModelDao searchHistoryRequestModelDao();

}
