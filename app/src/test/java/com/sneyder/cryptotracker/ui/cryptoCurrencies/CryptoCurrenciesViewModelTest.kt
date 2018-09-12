package com.sneyder.cryptotracker.ui.cryptoCurrencies

import android.arch.lifecycle.MutableLiveData
import com.nhaarman.mockito_kotlin.*
import com.sneyder.cryptotracker.TestingCoroutineContextProvider
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.utils.Resource
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CryptoCurrenciesViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: CryptoCurrenciesViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var cryptoCurrenciesRepository: CryptoCurrenciesRepository
    private val exchangeRate = ExchangeRate("USD", 1.0)

    @Before
    fun setUp(){
        cryptoCurrenciesRepository = mock()
        userRepository = mock()

        given(cryptoCurrenciesRepository.findCryptoCurrencies()).willReturn(Flowable.just(emptyList()))
        given(userRepository.findFavorites()).willReturn(Flowable.just(emptyList()))
        val currencySelection = MutableLiveData<Int>().also{ it.value = 0 }
        given(userRepository.currencySelection()).willReturn(currencySelection)
        given(cryptoCurrenciesRepository.findExchangeRateForSymbol(any())).willReturn(Flowable.just(exchangeRate))

        viewModel = CryptoCurrenciesViewModel(userRepository, cryptoCurrenciesRepository, TestingSchedulerProvider(), TestingCoroutineContextProvider(  ))
    }

    @Test
    fun `load CryptoCurrencies From LocalDb Without Favorites`() {
        // Given
        val cryptoCurrencies = listOf(CryptoCurrency(isFavorite = false))
        given(cryptoCurrenciesRepository.findCryptoCurrencies()).willReturn(Flowable.just(cryptoCurrencies))
        viewModel.favorites = emptyList()

        // When
        viewModel.loadCryptoCurrenciesFromLocalDb()

        // Then
        assertEquals(Resource.success(cryptoCurrencies), viewModel.cryptoCurrencies.blockingObserve())
    }

    @Test
    fun `load CryptoCurrencies From LocalDb With Favorites`() {
        // Given
        val cryptoCurrenciesNoFav = listOf(CryptoCurrency(isFavorite = false, id = 12), CryptoCurrency())
        val cryptoCurrenciesFav = listOf(CryptoCurrency(isFavorite = true, id = 12), CryptoCurrency())
        given(cryptoCurrenciesRepository.findCryptoCurrencies()).willReturn(Flowable.just(cryptoCurrenciesNoFav))
        viewModel.favorites = listOf(com.sneyder.cryptotracker.data.model.Favorite(currencyId = 12))

        // When
        viewModel.loadCryptoCurrenciesFromLocalDb()

        // Then
        assertEquals(Resource.success(cryptoCurrenciesFav), viewModel.cryptoCurrencies.blockingObserve())
    }

    @Test
    fun `exchangeRate As Expected`() {
        assertEquals(exchangeRate, viewModel.exchangeRate.blockingObserve())
    }

    @Test
    fun `logOut Successfully`() {
        // Given
        given(userRepository.logOut()).willReturn(Single.just("true"))

        // When
        viewModel.logOut()

        // Then
        assertEquals(true, viewModel.logOut.blockingObserve())
    }

    @Test
    fun `logOut Failed`(){
        // Given
        given(userRepository.logOut()).willReturn(Single.error(Throwable()))

        // When
        viewModel.logOut()

        // Then
        assertEquals(false, viewModel.logOut.blockingObserve())
    }

}