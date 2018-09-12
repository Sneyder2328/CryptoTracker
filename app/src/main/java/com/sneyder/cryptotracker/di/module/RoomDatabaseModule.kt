package com.sneyder.cryptotracker.di.module

import android.app.Application
import android.arch.persistence.room.Room
import com.sneyder.cryptotracker.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomDatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application) = Room.databaseBuilder(application, AppDatabase::class.java, "CryptoTrackerSiuu.db").build()

    @Singleton
    @Provides
    fun provideCryptoCurrencyDao(appDatabase: AppDatabase) = appDatabase.cryptoCurrencyDao()

    @Singleton
    @Provides
    fun providePriceAlertDao(appDatabase: AppDatabase) = appDatabase.priceAlertDao()

    @Singleton
    @Provides
    fun provideGlobalDataDao(appDatabase: AppDatabase) = appDatabase.globalDataDao()

    @Singleton
    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Singleton
    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase) = appDatabase.favoriteDao()

    @Singleton
    @Provides
    fun provideNewsArticleDao(appDatabase: AppDatabase) = appDatabase.newsArticleDao()

    @Singleton
    @Provides
    fun provideExchangeRateDao(appDatabase: AppDatabase) = appDatabase.exchangeRateDao()


    @Singleton
    @Provides
    fun provideTransactionDao(appDatabase: AppDatabase) = appDatabase.transactionDao()



}