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
import kotlinx.coroutines.experimental.launch
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
                            launch (coroutineContextProvider.CommonPool) {
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
        launch(coroutineContextProvider.CommonPool) {
            val eRate = cryptoCurrenciesRepository.findExchangeRateForSymbol(symbol).blockingFirst()
            currencySelectedExchangeRate.postValue(eRate)
        }
    }

}