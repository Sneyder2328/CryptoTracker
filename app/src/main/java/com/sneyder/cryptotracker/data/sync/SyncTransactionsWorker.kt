package com.sneyder.cryptotracker.data.sync

import androidx.work.Worker
import com.sneyder.cryptotracker.BaseApp
import debug
import javax.inject.Inject

class SyncTransactionsWorker : Worker(){

    @Inject
    lateinit var transactionsSynchronizer: TransactionsSynchronizer

    override fun doWork(): Result {
        debug("SyncTransactionsWorker doWork START")
        try {
            (applicationContext as BaseApp).appComponent.inject(this)
            transactionsSynchronizer.synchronizeTransactionsWithServer()
        } catch (e: Exception) {
            e.printStackTrace()
            debug("SyncTransactionsWorker RETRY")
            return Result.RETRY
        }
        debug("SyncTransactionsWorker doWork SUCCESS")
        return Result.SUCCESS
    }
}