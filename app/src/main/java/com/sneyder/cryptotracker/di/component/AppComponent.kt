package com.sneyder.cryptotracker.di.component

import android.app.Application
import com.sneyder.cryptotracker.BaseApp
import com.sneyder.cryptotracker.data.sync.*
import com.sneyder.cryptotracker.di.module.AppModule
import com.sneyder.cryptotracker.di.builder.ActivityBuilder
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.DaggerApplication;
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidSupportInjectionModule::class), (AppModule::class), (ActivityBuilder::class)])
interface AppComponent: AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

    fun inject(app: BaseApp)

    fun inject(worker: SyncFavoritesWorker)
    fun inject(worker: SyncTransactionsWorker)
    fun inject(worker: RefreshFavoritesWorker)
    fun inject(worker: RefreshTransactionsWorker)
    fun inject(worker: RefreshPriceAlertsWorker)
    fun inject(worker: SyncPriceAlertsWorker)

}