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

    @Provides
    fun provideCryptoCurrencyDao(appDatabase: AppDatabase) = appDatabase.cryptoCurrencyDao()

    @Provides
    fun providePriceAlertDao(appDatabase: AppDatabase) = appDatabase.priceAlertDao()

    @Provides
    fun provideGlobalDataDao(appDatabase: AppDatabase) = appDatabase.globalDataDao()

    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase) = appDatabase.favoriteDao()

    @Provides
    fun provideNewsArticleDao(appDatabase: AppDatabase) = appDatabase.newsArticleDao()

    @Provides
    fun provideExchangeRateDao(appDatabase: AppDatabase) = appDatabase.exchangeRateDao()

    @Provides
    fun provideTransactionDao(appDatabase: AppDatabase) = appDatabase.transactionDao()



}