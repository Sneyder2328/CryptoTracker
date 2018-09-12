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