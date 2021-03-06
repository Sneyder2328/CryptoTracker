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

package com.sneyder.cryptotracker.ui.globalData

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.sneyder.cryptotracker.TestingCoroutineContextProvider
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.GlobalData
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.GlobalDataRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.utils.Resource
import io.reactivex.Flowable
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class GlobalDataViewModelTest : BaseViewModelTest() {

    private lateinit var userRepository: UserRepository
    private lateinit var cryptoCurrenciesRepository: CryptoCurrenciesRepository
    private lateinit var globalDataRepository: GlobalDataRepository
    private lateinit var viewModel: GlobalDataViewModel

    @Before
    fun setUp() {
        userRepository = mock()
        cryptoCurrenciesRepository = mock()
        globalDataRepository = mock()
        viewModel = GlobalDataViewModel(userRepository, cryptoCurrenciesRepository, globalDataRepository, TestingSchedulerProvider(), TestingCoroutineContextProvider())
    }

    @Test
    fun `load GlobalData successfully`() {
        // Given
        val globalData = GlobalData("", "", "", "", "", "", "")
        given(globalDataRepository.findGlobalData()).willReturn(Flowable.just(globalData))

        // Then
        assertEquals(Resource.success(globalData), viewModel.getGlobalData().blockingObserve())
    }

    @Test
    fun `load GlobalData failed`() {
        // Given
        given(globalDataRepository.findGlobalData()).willReturn(Flowable.error(Throwable()))

        // Then
        assertEquals(Resource.error<GlobalData>(), viewModel.getGlobalData().blockingObserve())
    }

    @Test
    fun `load GlobalData only once`() {
        // Given
        given(globalDataRepository.findGlobalData()).willReturn(Flowable.just(GlobalData("", "", "", "", "", "", "")))

        // When
        viewModel.getGlobalData().blockingObserve()
        viewModel.getGlobalData().blockingObserve()

        // Then
        verify(globalDataRepository, times(1)).findGlobalData()
    }

    @Test
    fun `when currency Selected then return expected ExchangeRate`() {
        runBlocking {
            val (index, symbol) = Pair(0, "USD")
            val exchangeRate = ExchangeRate(symbol, 1.0)
            given(cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol)).willReturn(Flowable.just(exchangeRate))

            // When
            viewModel.currencySelected(index, symbol)

            //Then
            assertEquals(exchangeRate, viewModel.currencySelectedExchangeRate.blockingObserve())
        }
    }

}