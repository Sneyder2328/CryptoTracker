/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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