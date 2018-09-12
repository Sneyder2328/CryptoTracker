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