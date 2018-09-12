package com.sneyder.cryptotracker.data.sync

import androidx.work.Worker
import com.sneyder.cryptotracker.BaseApp
import debug
import javax.inject.Inject

class RefreshTransactionsWorker: Worker() {

    @Inject lateinit var transactionsSynchronizer: TransactionsSynchronizer

    override fun doWork(): Result {
        debug("RefreshTransactionsWorker doWork START")
        try {
            (applicationContext as BaseApp).appComponent.inject(this)
            transactionsSynchronizer.refreshTransactions()
        } catch (e: Exception) {
            e.printStackTrace()
            debug("RefreshTransactionsWorker RETRY")
            return Result.RETRY
        }
        debug("RefreshTransactionsWorker doWork SUCCESS")
        return Result.SUCCESS
    }

}