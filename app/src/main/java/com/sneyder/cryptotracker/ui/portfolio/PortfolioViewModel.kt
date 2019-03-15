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

package com.sneyder.cryptotracker.ui.portfolio

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.TransactionsByPairSet
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class PortfolioViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        schedulerProvider: SchedulerProvider,
        private val coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider) {

    val transactionsByPairSets: MutableLiveData<Resource<List<TransactionsByPairSet>>> by lazy { MutableLiveData<Resource<List<TransactionsByPairSet>>>() }

    private val cryptoCurrencies by lazy {
        cryptoCurrenciesRepository.findCryptoCurrencies().blockingFirst()
    }

    private fun getCryptoCurrencyPriceBySymbol(cryptoCurrencySymbol: String)
            = cryptoCurrencies.findLast { it.symbol == cryptoCurrencySymbol }?.price ?: 1.0

    init { loadTransactionsFromLocalDb() }

    private fun loadTransactionsFromLocalDb() {
        add(userRepository.findTransactions()
                .applySchedulers()
                .subscribeBy(
                        onNext = { transactions ->
                            GlobalScope.launch(coroutineContextProvider.CommonPool) {
                                val tradingPairs: MutableSet<String> = LinkedHashSet()
                                val transactionsByPairSets: MutableList<TransactionsByPairSet> = ArrayList()

                                transactions.forEach { tradingPairs.add(it.tradingPair) }
                                tradingPairs.forEach { tradingPair ->
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
                                    transactionsByPairSets.add(transactionsByPairSet)
                                }
                                this@PortfolioViewModel.transactionsByPairSets.postValue(Resource.success(transactionsByPairSets))
                            }
                        },
                        onError = {
                            //transactions.value = Resource.error()
                        },
                        onComplete = {}
                ))
    }

    val currencySelectedExchangeRate: MutableLiveData<ExchangeRate> by lazy { MutableLiveData<ExchangeRate>() }
    var currencySelected: String = ""
    var currencySelectedIndex = 0

    fun currencySelected(index: Int, symbol: String) {
        currencySelected = symbol
        currencySelectedIndex = index
        GlobalScope.launch(coroutineContextProvider.CommonPool) {
            val eRate = cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol).blockingFirst()
            currencySelectedExchangeRate.postValue(eRate)
        }
    }

}