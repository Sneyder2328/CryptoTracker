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

import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.repository.UserRepository
import debug
import error
import showValues

class TransactionsSynchronizer(
        private val userRepository: UserRepository) {

    fun synchronizeTransactionsWithServer() {
        // Launch coroutine to work in a background thread
        // Find all price alerts from the local database
        val transactions = userRepository.findAllTransactions().blockingFirst()

        // Find price alerts with syncStatus pending to add
        transactions.filter { it.syncStatus == SyncStatus.PENDING_TO_ADD }.also { it.showValues("transactions PENDING_TO_ADD") }.forEach { transactionToAdd ->
            // Add them in the server
            try {
                val addTransactionInServer = userRepository.addTransactionInServer(transactionToAdd.apply { userId = userRepository.getUserId() }).blockingGet()
                debug("addTransactionInServer = $addTransactionInServer")
                if (addTransactionInServer.successful) { // In case the response is successful, insert price alert with status synchronized in the DB
                    userRepository.insertTransaction(transactionToAdd.apply {
                        syncStatus = SyncStatus.SYNCHRONIZED
                        userId = userRepository.getUserId()
                    }).blockingAwait()
                }
            } catch (e: Exception) {
                error(e.message)
            }
        }

        // Find price alerts with syncStatus pending to update
        transactions.filter { it.syncStatus == SyncStatus.PENDING_TO_UPDATE }.also { it.showValues("transactions PENDING_TO_UPDATE") }.forEach { transactionToUpdate ->
            try {
                // Update them in server
                val updateTransactionInServer = userRepository.updateTransactionInServer(transactionToUpdate).blockingGet()
                debug("updateTransactionInServer = $updateTransactionInServer")
                if (updateTransactionInServer.successful) { // In case the response is successful, insert price alert with status synchronized in the DB
                    userRepository.insertTransaction(transactionToUpdate.apply { syncStatus = SyncStatus.SYNCHRONIZED }).blockingAwait()
                }
            } catch (e: Exception) {
                error(e.message)
            }
        }

        // Find price alerts with syncStatus pending to delete
        transactions.filter { it.syncStatus == SyncStatus.PENDING_TO_DELETE }.also { it.showValues("transactions PENDING_TO_DELETE") }.forEach { transactionPendingToDelete ->
            try {
                // Delete them in server
                val deleteTransactionInServer = userRepository.deleteTransactionInServer(transactionPendingToDelete.id).blockingGet()
                debug("deleteTransactionInServer = $deleteTransactionInServer")
                if (deleteTransactionInServer.successful) { // In case the response is successful, delete the price alert from the DB
                    userRepository.deleteTransaction(transactionPendingToDelete.id).blockingAwait()
                }
            } catch (e: Exception) {
                error(e.message)
            }
        }
    }

    fun refreshTransactions(){
        userRepository.refreshTransactions().blockingAwait()
    }

}