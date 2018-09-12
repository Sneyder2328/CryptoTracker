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
import com.sneyder.cryptotracker.data.model.ExchangeRate
import io.reactivex.Completable
import io.reactivex.Flowable

abstract class CryptoCurrenciesRepository {

    abstract fun insertCryptoCurrency(cryptoCurrency: CryptoCurrency): Completable

    abstract fun findCryptoCurrencies(): Flowable<List<CryptoCurrency>>

    abstract fun refreshCryptoCurrencies(): Completable


    abstract fun findExchangeRates(): Flowable<List<ExchangeRate>>

    abstract fun findExchangeRateForSymbol(symbol: String): Flowable<ExchangeRate>

    abstract fun refreshExchangeRates(): Completable

}