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

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.model.Transaction
import com.sneyder.utils.BaseDao
import io.reactivex.Flowable

@Dao
abstract class TransactionDao : BaseDao<Transaction> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTransactions(vararg transactions: Transaction)

    @Query("SELECT * FROM ${Transaction.TABLE_NAME} WHERE syncStatus != :syncStatus")
    abstract fun findTransactions(syncStatus: SyncStatus = SyncStatus.PENDING_TO_DELETE): Flowable<List<Transaction>>

    @Query("SELECT * FROM ${Transaction.TABLE_NAME}")
    abstract fun findAllTransactions(): Flowable<List<Transaction>>

    @Query("SELECT * FROM ${Transaction.TABLE_NAME} WHERE tradingPair = :tradingPair AND syncStatus != :syncStatus")
    abstract fun findTransactionsByTradingPair(tradingPair: String, syncStatus: SyncStatus = SyncStatus.PENDING_TO_DELETE): Flowable<List<Transaction>>

    @Query("DELETE FROM ${Transaction.TABLE_NAME}")
    abstract fun deleteTable()

    @Query("DELETE FROM ${Transaction.TABLE_NAME} WHERE id = :id")
    abstract fun deleteTransactionById(id: String)

    @Query("DELETE FROM ${Transaction.TABLE_NAME} WHERE syncStatus = :syncStatus")
    abstract fun deleteSynchronizedTransactions(syncStatus: SyncStatus = SyncStatus.SYNCHRONIZED)

}