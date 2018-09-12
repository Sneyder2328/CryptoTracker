package com.sneyder.cryptotracker.ui.portfolio

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.sneyder.cryptotracker.TestingCoroutineContextProvider
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.Transaction
import com.sneyder.cryptotracker.data.model.TransactionsByPairSet
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.utils.Resource
import io.reactivex.Flowable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PortfolioViewModelTest : BaseViewModelTest() {

    private lateinit var userRepository: UserRepository
    private lateinit var cryptoCurrenciesRepository: CryptoCurrenciesRepository
    private lateinit var viewModel: PortfolioViewModel
    private val transactions = listOf(Transaction(tradingPair = "BTC/USD", quantity = 0.1, price = 7000.0, fee = 1.0f))
    private val cryptoCurrencies = listOf(CryptoCurrency(priceStr = "7560", symbol = "BTC"))

    @Before
    fun setUp() {
        userRepository = mock{
            on { findTransactions() } doReturn Flowable.just(transactions)
        }
        cryptoCurrenciesRepository = mock{
            val exchangeRate = ExchangeRate("USD", 1.0)
            on { findExchangeRateForSymbol("USD") } doReturn Flowable.just(exchangeRate)
            on { findCryptoCurrencies() } doReturn Flowable.just(cryptoCurrencies)
        }
        viewModel = PortfolioViewModel(userRepository, cryptoCurrenciesRepository, TestingSchedulerProvider(), TestingCoroutineContextProvider())
    }

    @Test
    fun `transactions Loaded Successfully`() {
        // Given
        val transactionsByPairSets = listOf(TransactionsByPairSet(tradingPair = "BTC/USD", quantity = 0.1, quantityWithFees = 0.099, amount = 700.0, currentPrice = 7560.0, rateToUsd = 1.0))

        // Then
        assertEquals(Resource.success(transactionsByPairSets), viewModel.transactionsByPairSets.blockingObserve())
    }

    @Test
    fun `when currency Selected then return expected ExchangeRate`() {
        val (index, symbol) = Pair(0, "USD")
        val exchangeRate = ExchangeRate(symbol, 1.0)
        given(cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol)).willReturn(Flowable.just(exchangeRate))

        // When
        viewModel.currencySelected(index, symbol)

        //Then
        assertEquals(exchangeRate, viewModel.currencySelectedExchangeRate.blockingObserve())
    }
}