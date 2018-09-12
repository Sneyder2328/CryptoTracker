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

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class CurrencySelectorViewModel
@Inject constructor(
        private val cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    private var cryptoCurrencies: MutableLiveData<Resource<List<CryptoCurrency>>>? = null

    fun cryptoCurrencies(): MutableLiveData<Resource<List<CryptoCurrency>>> {
        if(cryptoCurrencies == null){
            cryptoCurrencies = MutableLiveData()
            add(cryptoCurrenciesRepository.findCryptoCurrencies()
                    .applySchedulers()
                    .subscribeBy(
                            onNext = { cryptoCurrencies?.value = Resource.success(it) },
                            onError = { cryptoCurrencies?.value = Resource.error() }
                    ))
        }
        return cryptoCurrencies!!
    }

}