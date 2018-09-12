package com.sneyder.cryptotracker.ui.currencyDetails

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.PriceAlert
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class CurrencyDetailsViewModel
@Inject constructor(
        private var userRepository: UserRepository,
        private var cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        schedulerProvider: SchedulerProvider,
        private var coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider) {

    fun getCurrency() = userRepository.getCurrencySelection(javaClass.name)

    val currencySelectedExchangeRate: MutableLiveData<ExchangeRate> by lazy { MutableLiveData<ExchangeRate>() }

    fun currencySelected(symbol: String) {
        launch(coroutineContextProvider.CommonPool) {
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
                }, { _ ->
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