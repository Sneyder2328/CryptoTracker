package com.sneyder.cryptotracker.data.repository

import com.sneyder.cryptotracker.data.api.CryptoCurrenciesApi
import com.sneyder.cryptotracker.data.database.GlobalDataDao
import com.sneyder.cryptotracker.data.model.GlobalData
import debug
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppGlobalDataRepository
@Inject constructor(
        private val cryptoCurrenciesApi: CryptoCurrenciesApi,
        private val globalDataDao: GlobalDataDao
) : GlobalDataRepository() {

    override fun findGlobalData(): Flowable<GlobalData> {
        return globalDataDao.findGlobalData()
                .doOnNext { debug("doOnNext findGlobalData()") }
                .doOnError { error("doOnError findGlobalData() = ${it.message}") }
    }

    override fun refreshGlobalData(): Completable {
        return cryptoCurrenciesApi.fetchGlobalData()
                .doOnSuccess { globalData->
                    debug("doOnSuccess refreshGlobalData() = $globalData")
                    globalDataDao.insert(globalData)
                }
                .doOnError { error("doOnError refreshGlobalData() = ${it.message}") }
                .toCompletable()
    }
}