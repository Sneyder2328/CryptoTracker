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