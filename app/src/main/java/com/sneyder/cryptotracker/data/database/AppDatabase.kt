package com.sneyder.cryptotracker.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.sneyder.cryptotracker.data.model.*

@Database(entities = [(CryptoCurrency::class), (PriceAlert::class), (GlobalData::class), (User::class), (Favorite::class), (ExchangeRate::class), (NewsArticle::class), (Transaction::class)], version = 1)
@TypeConverters(SyncStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cryptoCurrencyDao(): CryptoCurrencyDao

    abstract fun priceAlertDao(): PriceAlertDao

    abstract fun globalDataDao(): GlobalDataDao

    abstract fun userDao(): UserDao

    abstract fun newsArticleDao(): NewsArticleDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun exchangeRateDao(): ExchangeRateDao

    abstract fun transactionDao(): TransactionDao

    fun clearDatabase(){
        userDao().deleteTable()
        priceAlertDao().deleteTable()
        globalDataDao().deleteTable()
        cryptoCurrencyDao().deleteTable()
        favoriteDao().deleteTable()
        exchangeRateDao().deleteTable()
        newsArticleDao().deleteTable()
        transactionDao().deleteTable()
    }

}