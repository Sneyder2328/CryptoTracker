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

package com.sneyder.cryptotracker.ui.currencyDetails

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.PriceAlert
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.utils.CoroutineContextProvider
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CurrencyDetailsViewModel
@Inject constructor(
        private var userRepository: UserRepository,
        private var cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        schedulerProvider: SchedulerProvider,
        coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider, coroutineContextProvider) {

    fun getCurrency() = userRepository.getCurrencySelection(javaClass.name)

    val currencySelectedExchangeRate: MutableLiveData<ExchangeRate> by lazy { MutableLiveData<ExchangeRate>() }

    fun currencySelected(symbol: String) {
        GlobalScope.launch(IO) {
            val eRate = cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol).blockingFirst()
            currencySelectedExchangeRate.postValue(eRate)
        }
    }

    val priceAlert: MutableLiveData<Resource<PriceAlert>> by lazy { MutableLiveData<Resource<PriceAlert>>() }
    private var tradingPair: String = ""

    fun loadPriceAlertsByTradingPair(tradingPair: String) {
        this.tradingPair = tradingPair
        add(userRepository.findPriceAlertByTradingPair(tradingPair)
                .applySchedulers()
                .subscribe({ pa ->
                    priceAlert.value = Resource.success(pa)
                }, {
                    priceAlert.value = Resource.error()
                }))
    }

    fun insertPriceAlert(priceAlert: PriceAlert) {
        add(userRepository.insertPriceAlert(priceAlert)
                .applySchedulers()
                .subscribeBy(
                        onComplete = {},
                        onError = {}
                ))
    }


    fun updatePriceAlert(priceAlert: PriceAlert) {
        if (priceAlert.syncStatus == SyncStatus.SYNCHRONIZED) priceAlert.syncStatus = SyncStatus.PENDING_TO_UPDATE
        add(userRepository.updatePriceAlert(priceAlert)
                .applySchedulers()
                .subscribeBy(
                        onComplete = {},
                        onError = {}
                ))
    }

}