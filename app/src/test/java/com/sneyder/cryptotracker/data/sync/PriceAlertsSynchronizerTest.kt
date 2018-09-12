package com.sneyder.cryptotracker.data.sync

import com.nhaarman.mockito_kotlin.*
import com.sneyder.cryptotracker.data.model.PriceAlert
import com.sneyder.cryptotracker.data.model.SyncResponse
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test


class PriceAlertsSynchronizerTest {

    private lateinit var priceAlertsSynchronizer: PriceAlertsSynchronizer
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userRepository = mock()
        priceAlertsSynchronizer = PriceAlertsSynchronizer(userRepository)
    }

    @Test
    fun `when refresh PriceAlerts then refresh them`() {
        // Given
        given(userRepository.refreshPriceAlerts()).willReturn(Completable.complete())

        // When
        priceAlertsSynchronizer.refreshPriceAlerts()

        // Then
        verify(userRepository, times(1)).refreshPriceAlerts()
    }

    @Test
    fun `when all PriceAlerts are synchronized then do nothing`() {
        // Given
        val listOfSyncPriceAlerts = listOf(
                PriceAlert(id = "", priceAbove = 45.1, syncStatus = SyncStatus.SYNCHRONIZED),
                PriceAlert(id = "", priceAbove = 545445.1, syncStatus = SyncStatus.SYNCHRONIZED),
                PriceAlert(id = "", priceBelow = 4556.1, syncStatus = SyncStatus.SYNCHRONIZED)
        )
        given(userRepository.findAllPriceAlerts()).willReturn(Flowable.just(listOfSyncPriceAlerts))

        // When
        runBlocking { priceAlertsSynchronizer.synchronizePriceAlertsWithServer() }

        // Then
        verify(userRepository, times(0)).insertPriceAlert(any())
        verify(userRepository, times(0)).addPriceAlertInServer(any())
        verify(userRepository, times(0)).updatePriceAlertInServer(any())
        verify(userRepository, times(0)).deletePriceAlertInServer(any())
        verify(userRepository, times(0)).deletePriceAlert(any())
    }

    @Test
    fun `when some PriceAlerts are PendingToAdd then add them in server`() {
        // Given
        val priceAlertToAdd1 = PriceAlert(id = "", priceAbove = 45.1, syncStatus = SyncStatus.PENDING_TO_ADD)
        val priceAlerts = listOf(
                priceAlertToAdd1,
                PriceAlert(id = "", priceAbove = 545445.1, syncStatus = SyncStatus.SYNCHRONIZED),
                PriceAlert(id = "", priceAbove = 4556.1, syncStatus = SyncStatus.PENDING_TO_ADD)
        )
        given(userRepository.findAllPriceAlerts()).willReturn(Flowable.just(priceAlerts))
        given(userRepository.addPriceAlertInServer(any())).willReturn(Single.just(SyncResponse("add", true)))
        given(userRepository.insertPriceAlert(any())).willReturn(Completable.complete())

        // When
        runBlocking { priceAlertsSynchronizer.synchronizePriceAlertsWithServer() }

        // Then
        verify(userRepository, times(2)).addPriceAlertInServer(any())
        verify(userRepository, times(1)).insertPriceAlert(priceAlertToAdd1.also { it.syncStatus = SyncStatus.SYNCHRONIZED })
        verify(userRepository, times(0)).updatePriceAlertInServer(any())
        verify(userRepository, times(0)).deletePriceAlert(any())
        verify(userRepository, times(0)).deletePriceAlertInServer(any())
    }

    @Test
    fun `when some PriceAlerts are PendingToUpdate then update them in server`() {
        // Given
        val priceAlertToUpdate1 = PriceAlert(id = "", priceAbove = 45.1, syncStatus = SyncStatus.PENDING_TO_UPDATE)
        val priceAlerts = listOf(
                priceAlertToUpdate1,
                PriceAlert(id = "", priceAbove = 545445.1, syncStatus = SyncStatus.PENDING_TO_UPDATE),
                PriceAlert(id = "", priceAbove = 4556.1, syncStatus = SyncStatus.SYNCHRONIZED)
        )
        given(userRepository.findAllPriceAlerts()).willReturn(Flowable.just(priceAlerts))
        given(userRepository.updatePriceAlertInServer(any())).willReturn(Single.just(SyncResponse("update", true)))
        given(userRepository.insertPriceAlert(any())).willReturn(Completable.complete())

        // When
        runBlocking { priceAlertsSynchronizer.synchronizePriceAlertsWithServer() }

        // Then
        verify(userRepository, times(2)).updatePriceAlertInServer(any())
        verify(userRepository, times(1)).insertPriceAlert(priceAlertToUpdate1.also { it.syncStatus = SyncStatus.SYNCHRONIZED })
        verify(userRepository, times(0)).addPriceAlertInServer(any())
        verify(userRepository, times(0)).deletePriceAlert(any())
        verify(userRepository, times(0)).deletePriceAlertInServer(any())
    }

    @Test
    fun `when some PriceAlerts are PendingToDelete then delete them in server`() {
        // Given
        val priceAlerts = listOf(
                PriceAlert(id = "", priceAbove = 545445.1, syncStatus = SyncStatus.PENDING_TO_DELETE),
                PriceAlert(id = "", priceAbove = 4556.1, syncStatus = SyncStatus.PENDING_TO_DELETE)
        )
        given(userRepository.findAllPriceAlerts()).willReturn(Flowable.just(priceAlerts))
        given(userRepository.deletePriceAlertInServer(any())).willReturn(Single.just(SyncResponse("delete", true)))
        given(userRepository.insertPriceAlert(any())).willReturn(Completable.complete())

        // When
        runBlocking { priceAlertsSynchronizer.synchronizePriceAlertsWithServer() }

        // Then
        verify(userRepository, times(0)).addPriceAlertInServer(any())
        verify(userRepository, times(0)).updatePriceAlertInServer(any())
        verify(userRepository, times(2)).deletePriceAlert(any())
        verify(userRepository, times(2)).deletePriceAlertInServer(any())
    }
}