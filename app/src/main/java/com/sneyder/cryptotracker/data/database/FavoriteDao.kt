package com.sneyder.cryptotracker.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sneyder.cryptotracker.data.model.Favorite
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.utils.BaseDao
import io.reactivex.Flowable

@Dao
abstract class FavoriteDao : BaseDao<Favorite> {

    @Query("SELECT * FROM ${Favorite.TABLE_NAME}")
    abstract fun findFavorites(): Flowable<List<Favorite>>

    @Query("SELECT * FROM ${Favorite.TABLE_NAME} WHERE syncStatus != :syncStatus")
    abstract fun findFavoritesNotSync(syncStatus: SyncStatus = SyncStatus.SYNCHRONIZED): Flowable<List<Favorite>>

    @Query("DELETE FROM ${Favorite.TABLE_NAME} WHERE syncStatus = :syncStatus")
    abstract fun deleteSynchronizedFavorites(syncStatus: SyncStatus = SyncStatus.SYNCHRONIZED)

    @Query("DELETE FROM ${Favorite.TABLE_NAME} WHERE currencyId = :currencyId")
    abstract fun deleteFavoriteByCurrencyId(currencyId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFavorites(vararg favorites: Favorite)

    @Query("DELETE FROM ${Favorite.TABLE_NAME}")
    abstract fun deleteTable()
}
