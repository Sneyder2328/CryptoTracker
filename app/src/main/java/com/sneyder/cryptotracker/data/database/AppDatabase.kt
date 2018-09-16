/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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