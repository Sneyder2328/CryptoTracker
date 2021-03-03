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

package com.sneyder.cryptotracker.ui.main

import android.arch.lifecycle.MutableLiveData
import androidx.work.*
import androidx.work.PeriodicWorkRequestBuilder
import com.sneyder.cryptotracker.data.model.Screen
import com.sneyder.cryptotracker.data.model.User
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.data.sync.*
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import debug
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.work.WorkManager
import com.sneyder.utils.CoroutineContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val cryptoCurrenciesRepository: CryptoCurrenciesRepository,
        schedulerProvider: SchedulerProvider,
        coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider, coroutineContextProvider) {

    fun testMethod(){
        GlobalScope.launch(IO){
            (1..10).forEach {
                debug("do it $it")
                delay(1000)
            }
        }
    }

    private var user: MutableLiveData<Resource<User>>? = null

    fun getMyUser(): MutableLiveData<Resource<User>> {
        if (user == null) {
            user = MutableLiveData()
            loadUserFromDb()
        }
        return user!!
    }

    private fun loadUserFromDb() {
        add(userRepository.findMyUser()
                .applySchedulers()
                .subscribeBy(
                        onNext = { user?.value = Resource.success(it) },
                        onError = { user?.value = Resource.error() }
                ))
    }

    fun synchronizeDataWithServer(): Boolean {
        return if (userRepository.getLogged()) {
            debug("synchronizeDataWithServer")
            val myConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val periodicSyncPriceAlertsWork = PeriodicWorkRequestBuilder<SyncPriceAlertsWorker>(1, TimeUnit.HOURS)
                    .setConstraints(myConstraints)
                    .addTag("periodicSyncPriceAlertsWork")
                    .build()
            val periodicSyncTransactionsWork = PeriodicWorkRequestBuilder<SyncPriceAlertsWorker>(1, TimeUnit.HOURS)
                    .setConstraints(myConstraints)
                    .addTag("periodicSyncTransactionsWork")
                    .build()
            val periodicSyncFavoritesWork = PeriodicWorkRequestBuilder<SyncPriceAlertsWorker>(1, TimeUnit.HOURS)
                    .setConstraints(myConstraints)
                    .addTag("periodicSyncFavoritesWork")
                    .build()

            val syncPriceAlerts: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncPriceAlertsWorker>().addTag("syncPriceAlerts").setConstraints(myConstraints).build()
            val refreshPriceAlerts: OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshPriceAlertsWorker>().setConstraints(myConstraints).build()

            val syncTransactionsWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncTransactionsWorker>().addTag("syncTransactions").setConstraints(myConstraints).build()
            val refreshTransactionsWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshTransactionsWorker>().setConstraints(myConstraints).build()

            val syncFavoritesWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncFavoritesWorker>().addTag("syncFavorites").setConstraints(myConstraints).build()
            val refreshFavoritesWorker: OneTimeWorkRequest = OneTimeWorkRequestBuilder<RefreshFavoritesWorker>().setConstraints(myConstraints).build()

            WorkManager.getInstance().apply {
                enqueueUniquePeriodicWork("periodicSyncPriceAlertsWork", ExistingPeriodicWorkPolicy.KEEP, periodicSyncPriceAlertsWork)
                enqueueUniquePeriodicWork("periodicSyncTransactionsWork", ExistingPeriodicWorkPolicy.KEEP, periodicSyncTransactionsWork)
                enqueueUniquePeriodicWork("periodicSyncFavoritesWork", ExistingPeriodicWorkPolicy.KEEP, periodicSyncFavoritesWork)
                beginUniqueWork("syncPriceAlerts", ExistingWorkPolicy.KEEP, syncPriceAlerts)
                        .then(refreshPriceAlerts)
                        .enqueue()
                beginUniqueWork("syncTransactions", ExistingWorkPolicy.KEEP, syncTransactionsWorker)
                        .then(refreshTransactionsWorker)
                        .enqueue()
                beginUniqueWork("syncFavorites", ExistingWorkPolicy.KEEP, syncFavoritesWorker)
                        .then(refreshFavoritesWorker)
                        .enqueue()
            }
            true
        } else {
            WorkManager.getInstance().cancelAllWork()
            false
        }
    }


    fun keepFirebaseTokenIdUpdated(token: String?): Boolean {
        if (token == null) return false
        add(userRepository.updateFirebaseTokenId(userRepository.getSessionId(), token)
                .applySchedulers()
                .subscribeBy(
                        onSuccess = { },
                        onError = { }
                ))
        return true
    }

    fun getSortCurrenciesBy() = userRepository.getSortCurrenciesBySelection()

    fun setSortCurrenciesBy(sortBy: Int) = userRepository.setSortCurrenciesBySelection(sortBy)

    fun getCurrency() = userRepository.getCurrencySelection(javaClass.name)

    fun setCurrency(currency: Int) = userRepository.setCurrencySelection(javaClass.name, currency)

    fun setPercentChange(percentChange: Int) = userRepository.setPercentChangeSelection(percentChange)

    fun getPercentChange() = userRepository.getPercentChangeSelection()

    var currentScreen = getHomeScreen()

    private fun getHomeScreen() = userRepository.getHomeScreen()

    fun setHomeScreen(homeScreen: Screen) = userRepository.setHomeScreen(homeScreen)

    fun refreshExchangeRates() {
        add(cryptoCurrenciesRepository.refreshExchangeRates()
                .applySchedulers()
                .subscribeBy(
                        onError = {},
                        onComplete = {}
                ))
    }

}