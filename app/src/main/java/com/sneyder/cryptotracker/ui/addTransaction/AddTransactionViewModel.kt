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

import androidx.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.model.Transaction
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AddTransactionViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        schedulerProvider: SchedulerProvider,
        private val coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider) {

    val currencySelectedExchangeRate: MutableLiveData<ExchangeRate> by lazy { MutableLiveData<ExchangeRate>() }

    fun currencySelected(symbol: String){
        launch(coroutineContextProvider.CommonPool) {
            val eRate = cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol).blockingFirst()
            currencySelectedExchangeRate.postValue(eRate)
        }
    }

    fun insertTransaction(transaction: Transaction){
        add(userRepository.insertTransaction(transaction)
                .applySchedulers()
                .subscribeBy(
                        onComplete = {},
                        onError = {}
                ))
    }

    fun deleteTransaction(transaction: Transaction){
        insertTransaction(transaction.apply { syncStatus = SyncStatus.PENDING_TO_DELETE })
    }

}