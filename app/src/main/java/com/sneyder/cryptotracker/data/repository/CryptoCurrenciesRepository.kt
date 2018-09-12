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