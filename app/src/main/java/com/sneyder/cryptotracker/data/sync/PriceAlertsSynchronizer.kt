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

import com.sneyder.cryptotracker.data.model.PriceAlert
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.repository.UserRepository
import debug
import error
import showValues

class PriceAlertsSynchronizer(
        private val userRepository: UserRepository) {

    fun synchronizePriceAlertsWithServer() {
        // Launch coroutine to work in a background thread
        // Find all price alerts from the local database
        val priceAlerts: List<PriceAlert> = userRepository.findAllPriceAlerts().blockingFirst()

        // Find price alerts with syncStatus pending to add
        priceAlerts.filter { it.syncStatus == SyncStatus.PENDING_TO_ADD }.also { it.showValues("priceAlerts PENDING_TO_ADD") }.forEach { priceAlertToAdd ->
            // Add them in the server
            try {
                val addPriceAlertInServer = userRepository.addPriceAlertInServer(priceAlertToAdd).blockingGet()
                debug("addPriceAlertInServer = $addPriceAlertInServer")
                if (addPriceAlertInServer.successful) { // In case the response is successful, insert price alert with status synchronized in the DB
                    userRepository.insertPriceAlert(priceAlertToAdd.apply { syncStatus = SyncStatus.SYNCHRONIZED }).blockingAwait()
                }
            } catch (e: Exception) {
                error(e.message)
            }
        }

        // Find price alerts with syncStatus pending to update
        priceAlerts.filter { it.syncStatus == SyncStatus.PENDING_TO_UPDATE }.also { it.showValues("priceAlerts PENDING_TO_UPDATE") }.forEach { priceAlertToUpdate ->
            try {
                // Update them in server
                val updatePriceAlertInServer = userRepository.updatePriceAlertInServer(priceAlertToUpdate).blockingGet()
                debug("updatePriceAlertInServer = $updatePriceAlertInServer")
                if (updatePriceAlertInServer.successful) { // In case the response is successful, insert price alert with status synchronized in the DB
                    userRepository.insertPriceAlert(priceAlertToUpdate.apply { syncStatus = SyncStatus.SYNCHRONIZED }).blockingAwait()
                }
            } catch (e: Exception) {
                error(e.message)
            }
        }

        // Find price alerts with syncStatus pending to delete
        priceAlerts.filter { it.syncStatus == SyncStatus.PENDING_TO_DELETE }.also { it.showValues("priceAlerts PENDING_TO_DELETE") }.forEach { priceAlertPendingToDelete ->
            try {
                // Delete them in server
                val deletePriceAlertInServer = userRepository.deletePriceAlertInServer(priceAlertPendingToDelete.id).blockingGet()
                debug("deletePriceAlertInServer = $deletePriceAlertInServer")
                if (deletePriceAlertInServer.successful) { // In case the response is successful, delete the price alert from the DB
                    userRepository.deletePriceAlert(priceAlertPendingToDelete.id).blockingAwait()
                }
            } catch (e: Exception) {
                error(e.message)
            }
        }
    }

    fun refreshPriceAlerts(){
        userRepository.refreshPriceAlerts().blockingAwait()
    }


}