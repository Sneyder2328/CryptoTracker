package com.sneyder.cryptotracker.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.sneyder.cryptotracker.data.model.User
import com.sneyder.utils.BaseDao
import io.reactivex.Flowable

@Dao
abstract class UserDao : BaseDao<User> {

    @Query("SELECT * FROM ${User.TABLE_NAME} WHERE userId = :userId")
    abstract fun findById(userId: String): Flowable<User>

    @Query("DELETE FROM ${User.TABLE_NAME}")
    abstract fun deleteTable()

}