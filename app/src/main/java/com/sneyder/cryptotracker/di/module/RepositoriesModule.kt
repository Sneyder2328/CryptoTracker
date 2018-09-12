package com.sneyder.cryptotracker.di.module

import com.sneyder.cryptotracker.data.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoriesModule {

    @Provides
    @Singleton
    fun provideCryptoCurrenciesRepository(appCryptoCurrenciesRepository: AppCryptoCurrenciesRepository): CryptoCurrenciesRepository = appCryptoCurrenciesRepository

    @Provides
    @Singleton
    fun provideGlobalDataRepository(appGlobalDataRepository: AppGlobalDataRepository): GlobalDataRepository = appGlobalDataRepository

    @Provides
    @Singleton
    fun provideNewsRepository(appNewsRepository: AppNewsRepository): NewsRepository = appNewsRepository

    @Provides
    @Singleton
    fun provideUserRepository(appUserRepository: AppUserRepository): UserRepository = appUserRepository

}