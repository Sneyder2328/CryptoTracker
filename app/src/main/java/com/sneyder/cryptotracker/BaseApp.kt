package com.sneyder.cryptotracker

import android.content.Context
import android.support.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.sneyder.cryptotracker.di.component.AppComponent
import com.sneyder.cryptotracker.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

abstract class BaseApp: DaggerApplication() {

    lateinit var appComponent: AppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent =  DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }

    /**
     * Overriding this functions as a workaround to implement MultiDex, since this class cannot inherent from MultiDexApplication
     */
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}