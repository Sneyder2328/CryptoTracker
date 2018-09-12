package com.sneyder.cryptotracker.data.database

import android.arch.persistence.room.*
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