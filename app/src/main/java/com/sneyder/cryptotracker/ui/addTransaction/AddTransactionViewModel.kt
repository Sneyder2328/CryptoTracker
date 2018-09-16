package com.sneyder.cryptotracker.ui.addTransaction

import android.arch.lifecycle.MutableLiveData
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