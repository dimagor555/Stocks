package ru.dimagor555.stocks.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModel;
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModelDao;
import ru.dimagor555.stocks.data.local.stock.StockModel;
import ru.dimagor555.stocks.data.local.stock.StockModelDao;

@Database(entities = {StockModel.class, SearchHistoryRequestModel.class},
        version = 5, exportSchema = false)
public abstract class StocksDatabase extends RoomDatabase {
    private static final String DB_NAME = "stocks.db";

    private static volatile StocksDatabase instance;

    public abstract StockModelDao stockModelDao();

    public abstract SearchHistoryRequestModelDao searchHistoryRequestModelDao();

    public static StocksDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (StocksDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            StocksDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
