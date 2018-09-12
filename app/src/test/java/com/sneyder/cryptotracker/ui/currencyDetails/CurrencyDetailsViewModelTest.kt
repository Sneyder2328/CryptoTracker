package com.sneyder.cryptotracker.ui.currencyDetails

import com.nhaarman.mockito_kotlin.*
import com.sneyder.cryptotracker.TestingCoroutineContextProvider
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.PriceAlert
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.utils.Resource
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test

class CurrencyDetailsViewModelTest : BaseViewModelTest() {

    private lateinit var cryptoCurrenciesRepository: CryptoCurrenciesRepository
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: CurrencyDetailsViewModel

    @Before
    fun setUp() {
        userRepository = mock()
        cryptoCurrenciesRepository = mock()
        viewModel = CurrencyDetailsViewModel(userRepository, cryptoCurrenciesRepository, TestingSchedulerProvider(), TestingCoroutineContextProvider())
    }

    @Test
    fun `load PriceAlerts By TradingPair Successfully`() {
        // Given
        val tradingPair = "BTC/USD"
        val priceAlert = PriceAlert()
        given(userRepository.findPriceAlertByTradingPair(tradingPair)).willReturn(Single.just(priceAlert))

        // When
        viewModel.loadPriceAlertsByTradingPair(tradingPair)

        // Then
        assertEquals(Resource.success(priceAlert), viewModel.priceAlert.blockingObserve())
    }

    @Test
    fun `load PriceAlerts By TradingPair With Error`() {
        // Given
        val tradingPair = "BTC/USD"
        given(userRepository.findPriceAlertByTradingPair(tradingPair)).willReturn(Single.error(Throwable()))

        // When
        viewModel.loadPriceAlertsByTradingPair(tradingPair)

        // Then
        assertEquals(Resource.error<PriceAlert>(), viewModel.priceAlert.blockingObserve())
    }

    @Test
    fun `currency selected returns expected exchangeRate`() {
        // Given
        val symbol = "USD"
        val exchangeRate = ExchangeRate("USD", 1.0)
        given(cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol)).willReturn(Flowable.just(exchangeRate))

        // When
        viewModel.currencySelected(symbol)

        // Then
        assertEquals(exchangeRate, viewModel.currencySelectedExchangeRate.blockingObserve())
    }
}