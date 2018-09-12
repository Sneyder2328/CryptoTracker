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