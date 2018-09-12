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

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.GlobalData
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.GlobalDataRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class GlobalDataViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        private val globalDataRepository: GlobalDataRepository,
        schedulerProvider: SchedulerProvider,
        private val coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider) {

    private var globalData: MutableLiveData<Resource<GlobalData>>? = null

    fun getGlobalData(): MutableLiveData<Resource<GlobalData>> {
        if(globalData == null){
            globalData = MutableLiveData()
            loadGlobalDataFromLocalDb()
        }
        return globalData!!
    }

    fun getCurrency() = userRepository.getCurrencySelection(javaClass.name)

    val currencySelectedExchangeRate: MutableLiveData<ExchangeRate> by lazy { MutableLiveData<ExchangeRate>() }

    fun currencySelected(index: Int, symbol: String) {
        userRepository.setCurrencySelection(javaClass.name, index)
        launch(coroutineContextProvider.CommonPool) {
            val eRate = cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol).blockingFirst()
            currencySelectedExchangeRate.postValue(eRate)
        }
    }

    private fun loadGlobalDataFromLocalDb() {
        add(globalDataRepository.findGlobalData()
                .applySchedulers()
                .subscribeBy(
                        onNext = { globalData?.value = Resource.success(it) },
                        onError = { globalData?.value = Resource.error() },
                        onComplete = {}
                ))
    }

    fun refreshGlobalData() {
        globalData?.value = Resource.loading()
        loadGlobalDataFromServer()
    }

    private fun loadGlobalDataFromServer() {
        add(globalDataRepository.refreshGlobalData()
                .applySchedulers()
                .subscribeBy(
                        onError = {},
                        onComplete = {}
                ))
    }
}