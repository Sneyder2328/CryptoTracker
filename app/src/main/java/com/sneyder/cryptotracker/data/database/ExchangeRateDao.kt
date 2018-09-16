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

package com.sneyder.cryptotracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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