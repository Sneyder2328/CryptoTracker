package com.sneyder.cryptotracker.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.utils.BaseDao
import io.reactivex.Flowable

@Dao
abstract class CryptoCurrencyDao : BaseDao<CryptoCurrency> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCryptoCurrencies(vararg cryptoCurrencies: CryptoCurrency)

    @Query("SELECT * FROM ${CryptoCurrency.TABLE_NAME}")
    abstract fun findCryptoCurrencies(): Flowable<List<CryptoCurrency>>

    @Query("DELETE FROM ${CryptoCurrency.TABLE_NAME}")
    abstract fun deleteTable()
}
