package com.sneyder.cryptotracker.ui.currencySelector

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.utils.Resource
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class CurrencySelectorViewModelTest : BaseViewModelTest() {

    private lateinit var cryptoCurrenciesRepository: CryptoCurrenciesRepository
    private lateinit var viewModel: CurrencySelectorViewModel
    private val cryptoCurrencies = listOf(CryptoCurrency())

    @Before
    fun setUp() {
        cryptoCurrenciesRepository = mock()
        viewModel = CurrencySelectorViewModel(cryptoCurrenciesRepository, TestingSchedulerProvider())
    }

    @Test
    fun `load cryptocurrencies successfully`() {
        // Given
        given(cryptoCurrenciesRepository.findCryptoCurrencies()).willReturn(Flowable.just(cryptoCurrencies))

        // Then
        assertEquals(Resource.success(cryptoCurrencies), viewModel.cryptoCurrencies().blockingObserve())
    }

    @Test
    fun `load cryptocurrencies failed`() {
        // Given
        given(cryptoCurrenciesRepository.findCryptoCurrencies()).willReturn(Flowable.error(Throwable()))

        // Then
        assertEquals(Resource.error<List<CryptoCurrency>>(), viewModel.cryptoCurrencies().blockingObserve())
    }

    @Test
    fun `load cryptoCurrencies only once`() {
        // Given
        given(cryptoCurrenciesRepository.findCryptoCurrencies()).willReturn(Flowable.just(cryptoCurrencies))

        // When
        viewModel.cryptoCurrencies().blockingObserve()
        viewModel.cryptoCurrencies().blockingObserve()

        // Then
        verify(cryptoCurrenciesRepository, times(1)).findCryptoCurrencies()
    }

}