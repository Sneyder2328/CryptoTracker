package com.sneyder.cryptotracker.ui.transactionsByPairDetails

import com.nhaarman.mockito_kotlin.*
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
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class TransactionsByPairDetailsViewModelTest : BaseViewModelTest() {

    private lateinit var userRepository: UserRepository
    private lateinit var cryptoCurrenciesRepository: CryptoCurrenciesRepository
    private lateinit var viewModel: TransactionsByPairDetailsViewModel
    private val transactions = listOf(Transaction(tradingPair = "BTC/USD", quantity = 0.1, price = 7000.0, fee = 1.0f))
    private val cryptoCurrencies = listOf(CryptoCurrency(priceStr = "7560", symbol = "BTC"))

    @Before
    fun setUp() {
        userRepository = mock()
        cryptoCurrenciesRepository = mock()
        viewModel = TransactionsByPairDetailsViewModel(userRepository, cryptoCurrenciesRepository, TestingSchedulerProvider(), TestingCoroutineContextProvider())
    }

    @Test
    fun `load Transactions Only Once`() {
        // Given
        val tradingPair = "BTC/USD"
        given(userRepository.findTransactionsByTradingPair(tradingPair)).willReturn(Flowable.just(transactions))
        given(cryptoCurrenciesRepository.findCryptoCurrencies()).willReturn(Flowable.just(cryptoCurrencies))
        val exchangeRate = ExchangeRate("USD", 1.0)
        given(cryptoCurrenciesRepository.findExchangeRateForSymbol("USD")).willReturn(Flowable.just(exchangeRate))

        // When
        viewModel.loadTransactions(tradingPair)
        viewModel.loadTransactions(tradingPair)
        viewModel.loadTransactions(tradingPair)

        // Then
        verify(userRepository, times(1)).findTransactionsByTradingPair(tradingPair)
    }


    @Test
    fun `load Transactions Successfully`() {
        // Given
        val tradingPair = "BTC/USD"
        given(userRepository.findTransactionsByTradingPair(tradingPair)).willReturn(Flowable.just(transactions))
        given(cryptoCurrenciesRepository.findCryptoCurrencies()).willReturn(Flowable.just(cryptoCurrencies))
        val exchangeRate = ExchangeRate("USD", 1.0)
        given(cryptoCurrenciesRepository.findExchangeRateForSymbol("USD")).willReturn(Flowable.just(exchangeRate))

        val transactionsByPairSets = TransactionsByPairSet(
                tradingPair = "BTC/USD",
                quantity = 0.1,
                quantityWithFees = 0.099,
                amount = 700.0,
                currentPrice = 7560.0,
                rateToUsd = 1.0)

        // When
        viewModel.loadTransactions(tradingPair)

        // Then
        assertEquals(Resource.success(transactions), viewModel.transactions.blockingObserve())
        assertEquals(Resource.success(transactionsByPairSets), viewModel.transactionsByPairSet.blockingObserve())
    }
}