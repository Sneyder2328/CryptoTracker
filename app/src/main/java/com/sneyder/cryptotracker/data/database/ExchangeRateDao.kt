package com.sneyder.cryptotracker.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.utils.BaseDao
import io.reactivex.Flowable

@Dao
abstract class ExchangeRateDao : BaseDao<ExchangeRate> {

    @Query("SELECT * FROM ${ExchangeRate.TABLE_NAME}")
    abstract fun findExchangeRates(): Flowable<List<ExchangeRate>>

    @Query("SELECT * FROM ${ExchangeRate.TABLE_NAME} WHERE symbol = :symbol")
    abstract fun findExchangeRateForSymbol(symbol: String): Flowable<ExchangeRate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertExchangeRates(vararg exchangeRates: ExchangeRate)

    @Query("DELETE FROM ${ExchangeRate.TABLE_NAME}")
    abstract fun deleteTable()

}