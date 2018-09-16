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

class FavoritesSynchronizer(
        private val userRepository: UserRepository) {

    fun synchronizeFavoritesWithServer(){
        // Launch coroutine to work in a background thread
        // Find all favorites from the local database
        debug("synchronizeFavoritesWithServer START")
        val favorites = userRepository.findFavorites().blockingFirst()

        // Find favorites with syncStatus pending to add
        favorites.filter { it.syncStatus == SyncStatus.PENDING_TO_ADD }.also { it.showValues("favorites PENDING_TO_ADD") }.forEach { favoriteToAdd->
            try {
                val addFavoriteInServer =  userRepository.addFavoriteInServer(favoriteToAdd).blockingGet()
                debug("addFavoriteInServer = $addFavoriteInServer")
                if(addFavoriteInServer.successful) { // In case the response is successful, insert favorite with status sync in the DB
                    userRepository.insertFavorite(favoriteToAdd.apply { syncStatus = SyncStatus.SYNCHRONIZED }).blockingAwait()
                }
            } catch (e: Exception){
                error(e.message)
            }
        }

        // Find favorites with syncStatus pending to delete
        favorites.filter { it.syncStatus == SyncStatus.PENDING_TO_DELETE }.also { it.showValues("favorites PENDING_TO_DELETE") }.forEach { favoriteToDelete->
            try {
                val deleteFavoriteInServer = userRepository.deleteFavoriteInServer(favoriteToDelete).blockingGet()
                debug("deleteFavoriteInServer = $deleteFavoriteInServer")
                if(deleteFavoriteInServer.successful) { // In case the response is successful, delete the favorite from the DB
                    debug("deleteFavoriteByCurrencyId ${favoriteToDelete.currencyId} START")
                    userRepository.deleteFavoriteByCurrencyId(favoriteToDelete.currencyId).blockingAwait()
                    debug("deleteFavoriteByCurrencyId ${favoriteToDelete.currencyId} END")
                }
            } catch (e: Exception){
                error(e.message)
            }
        }
        debug("synchronizeFavoritesWithServer END")
    }


    fun refreshFavorites(){
        userRepository.refreshFavorites().blockingAwait()
    }

}