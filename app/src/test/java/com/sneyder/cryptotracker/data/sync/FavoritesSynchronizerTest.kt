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

import com.nhaarman.mockito_kotlin.*
import com.sneyder.cryptotracker.data.model.Favorite
import com.sneyder.cryptotracker.data.model.SyncResponse
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test

class FavoritesSynchronizerTest {

    private lateinit var favoritesSynchronizer: FavoritesSynchronizer
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userRepository = mock()
        favoritesSynchronizer = FavoritesSynchronizer(userRepository)
    }

    @Test
    fun `when refresh Favorites then refresh them`() {
        // Given
        given(userRepository.refreshFavorites()).willReturn(Completable.complete())

        // When
        favoritesSynchronizer.refreshFavorites()

        // Then
        verify(userRepository, times(1)).refreshFavorites()
    }

    @Test
    fun `when Favorites are synchronized then do nothing`() {
        // Given
        val favorites = listOf(
                Favorite(currencyId = 1024, syncStatus = SyncStatus.SYNCHRONIZED),
                Favorite(currencyId = 1, syncStatus = SyncStatus.SYNCHRONIZED),
                Favorite(currencyId = 64, syncStatus = SyncStatus.SYNCHRONIZED)
        )
        given(userRepository.findFavorites()).willReturn(Flowable.just(favorites))

        // When
        runBlocking { favoritesSynchronizer.synchronizeFavoritesWithServer() }

        // Then
        verify(userRepository, times(0)).deleteFavoriteInServer(any())
        verify(userRepository, times(0)).insertFavorite(any())
        verify(userRepository, times(0)).addFavoriteInServer(any())
        verify(userRepository, times(0)).deleteFavoriteByCurrencyId(any())
    }

    @Test
    fun `when some Favorites are PendingToAdd then add them in server`() {
        // Given
        val favorites = listOf(
                Favorite(currencyId = 1024, syncStatus = SyncStatus.PENDING_TO_ADD),
                Favorite(currencyId = 1, syncStatus = SyncStatus.SYNCHRONIZED),
                Favorite(currencyId = 64, syncStatus = SyncStatus.PENDING_TO_ADD)
        )
        given(userRepository.findFavorites()).willReturn(Flowable.just(favorites))
        given(userRepository.addFavoriteInServer(any())).willReturn(Single.just(SyncResponse(typeSync = "Add", successful = true)))
        given(userRepository.insertFavorite(any())).willReturn(Completable.complete())

        // When
        runBlocking { favoritesSynchronizer.synchronizeFavoritesWithServer() }

        // Then
        verify(userRepository, times(0)).deleteFavoriteInServer(any())
        verify(userRepository, times(2)).insertFavorite(any())
        verify(userRepository, times(2)).addFavoriteInServer(any())
        verify(userRepository, times(0)).deleteFavoriteByCurrencyId(any())
    }

    @Test
    fun `when some Favorites are PendingToDelete then delete them in server`() {
        // Given
        val favorites = listOf(
                Favorite(currencyId = 1024, syncStatus = SyncStatus.PENDING_TO_DELETE),
                Favorite(currencyId = 1, syncStatus = SyncStatus.SYNCHRONIZED),
                Favorite(currencyId = 64, syncStatus = SyncStatus.PENDING_TO_DELETE)
        )
        given(userRepository.findFavorites()).willReturn(Flowable.just(favorites))
        given(userRepository.deleteFavoriteInServer(any())).willReturn(Single.just(SyncResponse(typeSync = "Delete", successful = true)))
        given(userRepository.deleteFavoriteByCurrencyId(any())).willReturn(Completable.complete())

        // When
        runBlocking { favoritesSynchronizer.synchronizeFavoritesWithServer() }

        // Then
        verify(userRepository, times(2)).deleteFavoriteInServer(any())
        verify(userRepository, times(0)).insertFavorite(any())
        verify(userRepository, times(0)).addFavoriteInServer(any())
        verify(userRepository, times(2)).deleteFavoriteByCurrencyId(any())
    }


}