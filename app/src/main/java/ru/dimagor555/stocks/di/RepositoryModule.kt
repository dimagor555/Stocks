package ru.dimagor555.stocks.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRepository
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRepositoryImpl
import ru.dimagor555.stocks.data.model.stock.StockRepository
import ru.dimagor555.stocks.data.model.stock.StockRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    /*@Provides
    @Singleton
    fun provideStockRepository(
        localDatasource: LocalStockDatasource,
        remoteDatasource: RemoteStockDatasource
    ): StockRepository {
        return StockRepositoryImpl(localDatasource, remoteDatasource)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(
        localDatasource: LocalSearchHistoryRequestDatasource
    ): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(localDatasource)
    }*/

    @Binds
    @Singleton
    abstract fun provideStockRepository(impl: StockRepositoryImpl): StockRepository

    @Binds
    @Singleton
    abstract fun provideSearchHistoryRepository(
        impl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository
}