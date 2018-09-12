package com.sneyder.cryptotracker.ui.addTransaction

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.sneyder.cryptotracker.TestingCoroutineContextProvider
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class AddTransactionViewModelTest : BaseViewModelTest() {

    private lateinit var userRepository: UserRepository
    private lateinit var cryptoCurrenciesRepository: CryptoCurrenciesRepository
    private lateinit var viewModel: AddTransactionViewModel

    @Before
    fun setUp() {
        userRepository = mock()
        cryptoCurrenciesRepository = mock()
        viewModel = AddTransactionViewModel(userRepository, cryptoCurrenciesRepository, TestingSchedulerProvider(), TestingCoroutineContextProvider())
    }

    @Test
    fun `when currency selected then update exchange rate`() {
        // Given
        val symbol = "USD"
        val exchangeRate = ExchangeRate(symbol, 1.0)
        given(cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol)).willReturn(Flowable.just(exchangeRate))

        // When
        viewModel.currencySelected(symbol)

        // Then
        assertEquals(exchangeRate, viewModel.currencySelectedExchangeRate.blockingObserve())
    }

}