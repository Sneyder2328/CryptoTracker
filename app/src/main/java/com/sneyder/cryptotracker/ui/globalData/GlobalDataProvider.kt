package com.sneyder.cryptotracker.ui.globalData

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class GlobalDataProvider {

    @ContributesAndroidInjector
    abstract fun provideGlobalDataFragment(): GlobalDataFragment

}