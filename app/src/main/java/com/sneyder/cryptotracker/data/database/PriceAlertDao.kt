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
import com.sneyder.cryptotracker.data.model.PriceAlert
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.utils.BaseDao
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class PriceAlertDao: BaseDao<PriceAlert> {

    @Query("SELECT * FROM ${PriceAlert.TABLE_NAME} WHERE tradingPair = :tradingPair")
    abstract fun findPriceAlertByTradingPair(tradingPair: String): Single<PriceAlert>

    @Query("SELECT * FROM ${PriceAlert.TABLE_NAME} WHERE syncStatus != :syncStatus")
    abstract fun findPriceAlertsNotSync(syncStatus: SyncStatus = SyncStatus.SYNCHRONIZED): Flowable<List<PriceAlert>>

    @Query("SELECT * FROM ${PriceAlert.TABLE_NAME}")
    abstract fun findAllPriceAlerts(): Flowable<List<PriceAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPriceAlerts(vararg priceAlerts: PriceAlert)

    @Query("DELETE FROM ${PriceAlert.TABLE_NAME} WHERE syncStatus = :syncStatus")
    abstract fun deleteSynchronizedPriceAlerts(syncStatus: SyncStatus = SyncStatus.SYNCHRONIZED)

    @Query("DELETE FROM ${PriceAlert.TABLE_NAME} WHERE id = :id")
    abstract fun deletePriceAlertById(id: String)

    @Query("DELETE FROM ${PriceAlert.TABLE_NAME}")
    abstract fun deleteTable()

}