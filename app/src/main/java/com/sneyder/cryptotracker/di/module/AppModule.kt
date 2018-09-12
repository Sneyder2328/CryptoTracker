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