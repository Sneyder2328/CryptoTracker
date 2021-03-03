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

package com.sneyder.cryptotracker.ui.currencySelector

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.sneyder.cryptotracker.TestingCoroutineContextProvider
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
        viewModel = CurrencySelectorViewModel(cryptoCurrenciesRepository, TestingSchedulerProvider(), TestingCoroutineContextProvider())
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