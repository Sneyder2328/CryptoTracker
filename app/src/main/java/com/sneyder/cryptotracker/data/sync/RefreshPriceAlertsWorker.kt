package com.sneyder.cryptotracker.data.sync

import androidx.work.Worker
import com.sneyder.cryptotracker.BaseApp
import debug
import javax.inject.Inject

class RefreshPriceAlertsWorker  : Worker() {

    @Inject lateinit var priceAlertsSynchronizer: PriceAlertsSynchronizer

    override fun doWork(): Result {
        debug("RefreshPriceAlertsWorker doWork START")
        try {
            (applicationContext as BaseApp).appComponent.inject(this)
            priceAlertsSynchronizer.refreshPriceAlerts()
        } catch (e: Exception) {
            e.printStackTrace()
            debug("RefreshPriceAlertsWorker RETRY")
            return Result.RETRY
        }
        debug("RefreshPriceAlertsWorker doWork SUCCESS")
        return Result.SUCCESS
    }
}