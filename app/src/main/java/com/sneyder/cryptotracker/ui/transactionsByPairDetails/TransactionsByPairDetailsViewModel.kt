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

package com.sneyder.cryptotracker.ui.transactionsByPairDetails

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.model.Transaction
import com.sneyder.cryptotracker.data.model.TransactionsByPairSet
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class TransactionsByPairDetailsViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        schedulerProvider: SchedulerProvider,
        private val coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider) {

    private val cryptoCurrencies by lazy { cryptoCurrenciesRepository.findCryptoCurrencies().blockingFirst() }

    val transactions: MutableLiveData<Resource<List<Transaction>>> by lazy { MutableLiveData<Resource<List<Transaction>>>() }
    val transactionsByPairSet: MutableLiveData<Resource<TransactionsByPairSet>> by lazy { MutableLiveData<Resource<TransactionsByPairSet>>() }

    private var tradingPair: String = ""

    fun loadTransactions(tradingPair: String) {
        if(this.tradingPair == tradingPair){
            return
        }
        this.tradingPair = tradingPair
        add(userRepository.findTransactionsByTradingPair(tradingPair)
                .applySchedulers()
                .subscribeBy(
                        onNext = { transactions->
                            launch(coroutineContextProvider.CommonPool) {
                                val transactionsByPairSet = TransactionsByPairSet(tradingPair)
                                val symbol = tradingPair.substring(tradingPair.indexOf("/")+1)
                                val symbolCrypto = tradingPair.substring(0, tradingPair.indexOf("/"))
                                val eRate = cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol).blockingFirst()
                                transactionsByPairSet.rateToUsd = eRate.rate
                                transactionsByPairSet.currentPrice = eRate.rate * getCryptoCurrencyPriceBySymbol(symbolCrypto)
                                transactions.filter { it.tradingPair == tradingPair }.forEach {
                                    transactionsByPairSet.quantityWithFees += it.quantityMinusFee()
                                    transactionsByPairSet.quantity += it.quantity
                                    if (it.isABuy()) transactionsByPairSet.amount += it.totalCost()
                                    else transactionsByPairSet.amount -= it.totalCost()
                                }

                                this@TransactionsByPairDetailsViewModel.transactions.postValue(Resource.success(transactions.sortedByDescending { it.date }))
                                this@TransactionsByPairDetailsViewModel.transactionsByPairSet.postValue(Resource.success(transactionsByPairSet))
                            }
                        },
                        onError = {}
                ))
    }


    private fun getCryptoCurrencyPriceBySymbol(cryptoCurrencySymbol: String)
            = cryptoCurrencies.findLast { it.symbol == cryptoCurrencySymbol }?.price ?: 1.0

}