package com.sneyder.cryptotracker.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = Favorite.TABLE_NAME)
data class Favorite(
        @SerializedName("userId") @Expose var userId: String = "",
        @SerializedName("currencyId") @Expose @PrimaryKey var currencyId: Int = 0,
        @Expose(serialize = false, deserialize = false) @TypeConverters(SyncStatusConverter::class) var syncStatus: SyncStatus = SyncStatus.PENDING_TO_ADD) {
    companion object {
        const val TABLE_NAME = "Favorite"
    }
}