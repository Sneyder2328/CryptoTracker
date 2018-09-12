package com.sneyder.cryptotracker.ui.news

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NewsProvider {

    @ContributesAndroidInjector
    abstract fun provideNewsFragment(): NewsFragment

}