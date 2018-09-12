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
import android.content.Context
import android.content.SharedPreferences
import com.sneyder.cryptotracker.data.hashGenerator.AppHasher
import com.sneyder.cryptotracker.data.hashGenerator.Hasher
import com.sneyder.cryptotracker.data.sync.TransactionsSynchronizer
import com.sneyder.cryptotracker.data.preferences.AppPreferencesHelper
import com.sneyder.cryptotracker.data.preferences.PreferencesHelper
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.data.sync.FavoritesSynchronizer
import com.sneyder.cryptotracker.data.sync.PriceAlertsSynchronizer
import com.sneyder.cryptotracker.utils.AppCoroutineContextProvider
import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import com.sneyder.utils.schedulers.AppSchedulerProvider
import com.sneyder.utils.schedulers.SchedulerProvider
import dagger.Module
import dagger.Provides
import defaultSharedPreferences
import javax.inject.Singleton

@Module(includes = [(ViewModelModule::class), (RepositoriesModule::class), (RoomDatabaseModule::class), (RetrofitModule::class)])
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences = context.defaultSharedPreferences

    @Provides
    @Singleton
    fun providePreferencesHelper(appPreferencesHelper: AppPreferencesHelper): PreferencesHelper = appPreferencesHelper

    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

    @Provides
    @Singleton
    fun provideCoroutineContextProvider(): CoroutineContextProvider = AppCoroutineContextProvider()

    @Provides
    @Singleton
    fun provideHasher(): Hasher = AppHasher()

    @Provides
    @Singleton
    fun providePriceAlertsSynchronizer(userRepository: UserRepository): PriceAlertsSynchronizer
            = PriceAlertsSynchronizer(userRepository)

    @Provides
    @Singleton
    fun provideFavoritesSynchronizer(userRepository: UserRepository): FavoritesSynchronizer
            = FavoritesSynchronizer(userRepository)


    @Provides
    @Singleton
    fun provideTransactionsSynchronizer(userRepository: UserRepository): TransactionsSynchronizer
            = TransactionsSynchronizer(userRepository)

}