package ru.dimagor555.stocks.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.dimagor555.stocks.data.local.StocksDatabase
import ru.dimagor555.stocks.data.local.searchhistory.SearchHistoryRequestModelDao
import ru.dimagor555.stocks.data.local.stock.StockModelDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): StocksDatabase {
        return Room.databaseBuilder(
            appContext,
            StocksDatabase::class.java,
            StocksDatabase.DB_NAME
        ).build()
    }

    @Provides
    fun provideStockModelDao(database: StocksDatabase): StockModelDao {
        return database.stockModelDao()
    }

    @Provides
    fun provideSearchHistoryRequestModelDao(
        database: StocksDatabase
    ): SearchHistoryRequestModelDao {
        return database.searchHistoryRequestModelDao()
    }
}