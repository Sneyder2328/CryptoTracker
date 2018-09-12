package com.sneyder.cryptotracker.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.sneyder.cryptotracker.data.model.GlobalData
import com.sneyder.utils.BaseDao
import io.reactivex.Flowable

@Dao
abstract class GlobalDataDao : BaseDao<GlobalData> {

    @Query("SELECT * FROM ${GlobalData.TABLE_NAME} LIMIT 1")
    abstract fun findGlobalData(): Flowable<GlobalData>

    @Query("DELETE FROM ${GlobalData.TABLE_NAME}")
    abstract fun deleteTable()

}