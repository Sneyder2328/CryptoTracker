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

import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.database.CryptoCurrencyDao
import com.sneyder.cryptotracker.data.api.CryptoCurrenciesApi
import com.sneyder.cryptotracker.data.database.ExchangeRateDao
import com.sneyder.cryptotracker.data.model.ExchangeRate
import debug
import error
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCryptoCurrenciesRepository
@Inject constructor(
        private val cryptoCurrenciesApi: CryptoCurrenciesApi,
        private val cryptoCurrencyDao: CryptoCurrencyDao,
        private val exchangeRateDao: ExchangeRateDao
): CryptoCurrenciesRepository() {

    override fun insertCryptoCurrency(cryptoCurrency: CryptoCurrency): Completable {
        return Completable.create { cryptoCurrencyDao.insert(cryptoCurrency) }
                .doOnComplete { debug("doOnComplete insertCryptoCurrency($cryptoCurrency)") }
                .doOnError { error("doOnError insertCryptoCurrency($cryptoCurrency)") }
    }

    override fun findCryptoCurrencies(): Flowable<List<CryptoCurrency>> {
        return cryptoCurrencyDao.findCryptoCurrencies()
                .doOnNext { debug("doOnNext findCryptoCurrencies()") }
                .doOnError { error("doOnError findCryptoCurrencies() = ${it.message}") }
    }

    override fun refreshCryptoCurrencies(): Completable {
        return cryptoCurrenciesApi.fetchCryptoCurrencies()
                .doOnSuccess { currencies->
                    debug("doOnSuccess refreshCryptoCurrencies()")
                    cryptoCurrencyDao.insertCryptoCurrencies(*currencies.toTypedArray())
                }
                .doOnError { error("doOnError refreshCryptoCurrencies() = ${it.message}") }
                .toCompletable()
    }

    override fun findExchangeRates(): Flowable<List<ExchangeRate>> {
        return exchangeRateDao.findExchangeRates()
                .doOnNext { debug("doOnNext findExchangeRates() = $it") }
                .doOnError { error("doOnError findExchangeRates() = ${it.message}") }
    }

    override fun findExchangeRateForSymbol(symbol: String): Flowable<ExchangeRate> {
        return exchangeRateDao.findExchangeRateForSymbol(symbol)
                .doOnNext { debug("doOnNext findExchangeRateForSymbol($symbol) = $it") }
                .doOnError { error("doOnError findExchangeRateForSymbol($symbol) = ${it.message}") }
    }


    override fun refreshExchangeRates(): Completable {
        return cryptoCurrenciesApi.getExchangeRates()
                .doOnSuccess { exchangeRates->
                    debug("doOnSuccess refreshExchangeRates() =  $exchangeRates")
                    exchangeRateDao.insertExchangeRates(*exchangeRates.toTypedArray())
                }
                .doOnError { error("doOnError refreshExchangeRates() = ${it.message}") }
                .toCompletable()
    }
}