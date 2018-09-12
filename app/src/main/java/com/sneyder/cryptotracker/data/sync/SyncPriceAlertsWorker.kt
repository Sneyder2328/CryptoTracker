package com.sneyder.cryptotracker.data.sync

import androidx.work.Worker
import com.sneyder.cryptotracker.BaseApp
import debug
import javax.inject.Inject

class SyncPriceAlertsWorker : Worker() {

    @Inject lateinit var priceAlertsSynchronizer: PriceAlertsSynchronizer

    override fun doWork(): Result {
        debug("SyncPriceAlertsWorker doWork START")
        try {
            (applicationContext as BaseApp).appComponent.inject(this)
            priceAlertsSynchronizer.synchronizePriceAlertsWithServer()
        } catch (e: Exception) {
            e.printStackTrace()
            debug("SyncPriceAlertsWorker doWork RETRY")
            return Result.RETRY
        }
        debug("SyncPriceAlertsWorker doWork SUCCESS")
        return Result.SUCCESS
    }
}