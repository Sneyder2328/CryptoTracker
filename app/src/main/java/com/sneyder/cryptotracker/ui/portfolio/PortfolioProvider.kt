package com.sneyder.cryptotracker.ui.portfolio

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PortfolioProvider {

    @ContributesAndroidInjector
    abstract fun providePortfolioFragment(): PortfolioFragment

}