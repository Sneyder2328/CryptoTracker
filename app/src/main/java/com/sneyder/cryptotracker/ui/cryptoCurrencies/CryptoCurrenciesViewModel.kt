package com.sneyder.cryptotracker.ui.cryptoCurrencies

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.Favorite
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.utils.Constants
import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import debug
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class CryptoCurrenciesViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        schedulerProvider: SchedulerProvider,
        private var coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider) {

    val cryptoCurrencies: MutableLiveData<Resource<List<CryptoCurrency>>> by lazy { MutableLiveData<Resource<List<CryptoCurrency>>>() }

    val percentChangeSelection by lazy { userRepository.percentChangeSelection() }
    val sortCurrenciesBy by lazy { userRepository.sortCurrenciesBySelection() }

    val exchangeRate: MediatorLiveData<ExchangeRate> = MediatorLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var favorites: List<Favorite> = ArrayList()
    private var currencies: List<CryptoCurrency> = ArrayList()

    val logOut: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    init {
        loadCryptoCurrenciesFromLocalDb()
        loadFavorites()
        exchangeRate.addSource(userRepository.currencySelection()) { currency ->
            launch(coroutineContextProvider.CommonPool) {
                val eR = cryptoCurrenciesRepository.findExchangeRateForSymbol(Constants.CURRENCIES[currency!!]).blockingFirst()
                exchangeRate.postValue(eR)
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun loadCryptoCurrenciesFromLocalDb() {
        add(cryptoCurrenciesRepository.findCryptoCurrencies()
                .applySchedulers()
                .subscribeBy(
                        onNext = {
                            debug("findCryptoCurrencies onNext")
                            currencies = it
                            updateCurrencies()
                        },
                        onError = { cryptoCurrencies.value = Resource.error() },
                        onComplete = {}
                ))
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateCurrencies() {
        currencies.forEach { currency ->
            currency.isFavorite = favorites.any { it.currencyId == currency.id }
        }
        cryptoCurrencies.value = Resource.success(currencies)
    }

    private fun loadFavorites() {
        add(userRepository.findFavorites()
                .applySchedulers()
                .subscribeBy(
                        onNext = { it ->
                            favorites = it.filter { it.syncStatus != SyncStatus.PENDING_TO_DELETE }
                            updateCurrencies()
                        },
                        onError = {}
                ))
    }

    fun refreshCryptoCurrenciesData() {
        cryptoCurrencies.value = Resource.loading(currencies)
        add(cryptoCurrenciesRepository.refreshCryptoCurrencies()
                .applySchedulers()
                .subscribeBy(
                        onError = { cryptoCurrencies.value = Resource.success(currencies) }
                ))
    }


    fun updateFavorite(favorite: Favorite) {
        add(userRepository.insertFavorite(favorite)
                .applySchedulers()
                .subscribeBy(
                        onError = {  }
                ))
    }


    fun logOut() {
        add(userRepository.logOut()
                .applySchedulers()
                .subscribeBy(
                        onError = { logOut.value = false },
                        onSuccess = { logOut.value = true }
                ))
    }

    val isLoggedIn: Boolean
        get() = userRepository.getLogged()

}