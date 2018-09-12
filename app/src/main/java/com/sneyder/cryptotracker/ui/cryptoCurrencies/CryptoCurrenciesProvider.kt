package com.sneyder.cryptotracker.ui.cryptoCurrencies

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CryptoCurrenciesProvider {

    @ContributesAndroidInjector()
    abstract fun provideCryptoCurrenciesFragment(): CryptoCurrenciesFragment

}