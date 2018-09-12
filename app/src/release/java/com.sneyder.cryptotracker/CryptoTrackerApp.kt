package com.sneyder.cryptotracker

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class CryptoTrackerApp: BaseApp() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
    }

}