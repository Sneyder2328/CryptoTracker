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
import com.sneyder.cryptotracker.data.model.SyncResponse
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.model.Transaction
import com.sneyder.cryptotracker.data.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TransactionsSynchronizerTest {

    private lateinit var userRepository: UserRepository
    private lateinit var transactionsSynchronizer: TransactionsSynchronizer

    @Before
    fun setUp() {
        userRepository = mock()
        transactionsSynchronizer = TransactionsSynchronizer(userRepository)
    }

    @Test
    fun `when refresh PriceAlerts then refresh them`() {
        // Given
        given(userRepository.refreshTransactions()).willReturn(Completable.complete())

        // When
        transactionsSynchronizer.refreshTransactions()

        // Then
        verify(userRepository, times(1)).refreshTransactions()
    }

    @Test
    fun `when all transactions are synchronized then do nothing`() {
        // Given
        val transactions = listOf(
                Transaction(id = "564", syncStatus = SyncStatus.SYNCHRONIZED),
                Transaction(id = "54", syncStatus = SyncStatus.SYNCHRONIZED),
                Transaction(id = "584", syncStatus = SyncStatus.SYNCHRONIZED)
        )
        given(userRepository.findAllTransactions()).willReturn(Flowable.just(transactions))

        // When
        runBlocking { transactionsSynchronizer.synchronizeTransactionsWithServer() }

        // Then
        verify(userRepository, times(0)).deleteTransactionInServer(any())
        verify(userRepository, times(0)).deleteTransaction(any())
        verify(userRepository, times(0)).insertTransaction(any())
        verify(userRepository, times(0)).addTransactionInServer(any())
        verify(userRepository, times(0)).updateTransactionInServer(any())
    }

    @Test
    fun `when some transactions are PendingToAdd then add them in server`() {
        // Given
        val transactions = listOf(
                Transaction(id = "564", syncStatus = SyncStatus.PENDING_TO_ADD),
                Transaction(id = "54", syncStatus = SyncStatus.PENDING_TO_ADD),
                Transaction(id = "584", syncStatus = SyncStatus.SYNCHRONIZED))
        given(userRepository.getUserId()).willReturn("45464565456")
        given(userRepository.findAllTransactions()).willReturn(Flowable.just(transactions))
        given(userRepository.insertTransaction(any())).willReturn(Completable.complete())
        given(userRepository.addTransactionInServer(any())).willReturn(Single.just(SyncResponse(typeSync = "add", successful = true)))

        // When
        runBlocking { transactionsSynchronizer.synchronizeTransactionsWithServer() }

        // Then
        verify(userRepository, times(2)).insertTransaction(any())
        verify(userRepository, times(2)).addTransactionInServer(any())
        verify(userRepository, times(0)).updateTransactionInServer(any())
        verify(userRepository, times(0)).deleteTransactionInServer(any())
        verify(userRepository, times(0)).deleteTransaction(any())
    }

    @Test
    fun `when some transactions are PendingToUpdate then update them in server`() {
        // Given
        val pendingToUpdate1 = Transaction(id = "564", syncStatus = SyncStatus.PENDING_TO_UPDATE)
        val transactions = listOf(
                pendingToUpdate1,
                Transaction(id = "54", syncStatus = SyncStatus.PENDING_TO_UPDATE),
                Transaction(id = "584", syncStatus = SyncStatus.SYNCHRONIZED))
        given(userRepository.findAllTransactions()).willReturn(Flowable.just(transactions))
        given(userRepository.insertTransaction(any())).willReturn(Completable.complete())
        given(userRepository.updateTransactionInServer(any())).willReturn(Single.just(SyncResponse(typeSync = "update", successful = true)))

        // When
        runBlocking { transactionsSynchronizer.synchronizeTransactionsWithServer() }

        // Then
        verify(userRepository, times(2)).insertTransaction(any())
        verify(userRepository, times(2)).updateTransactionInServer(any())
        verify(userRepository, times(1)).updateTransactionInServer(pendingToUpdate1.apply { syncStatus = SyncStatus.SYNCHRONIZED })
        verify(userRepository, times(0)).addTransactionInServer(any())
        verify(userRepository, times(0)).deleteTransactionInServer(any())
        verify(userRepository, times(0)).deleteTransaction(any())
    }

    @Test
    fun `when some transactions are PendingToDelete then delete them in server`() {
        // Given
        val transactions = listOf(
                Transaction(id = "564", syncStatus = SyncStatus.PENDING_TO_DELETE),
                Transaction(id = "54", syncStatus = SyncStatus.PENDING_TO_DELETE),
                Transaction(id = "584", syncStatus = SyncStatus.SYNCHRONIZED))
        given(userRepository.findAllTransactions()).willReturn(Flowable.just(transactions))
        given(userRepository.deleteTransaction(any())).willReturn(Completable.complete())
        given(userRepository.deleteTransactionInServer(any())).willReturn(Single.just(SyncResponse(typeSync = "delete", successful = true)))

        // When
        runBlocking { transactionsSynchronizer.synchronizeTransactionsWithServer() }

        // Then
        verify(userRepository, times(2)).deleteTransactionInServer(any())
        verify(userRepository, times(2)).deleteTransaction(any())
        verify(userRepository, times(0)).insertTransaction(any())
        verify(userRepository, times(0)).addTransactionInServer(any())
        verify(userRepository, times(0)).updateTransactionInServer(any())
    }

}