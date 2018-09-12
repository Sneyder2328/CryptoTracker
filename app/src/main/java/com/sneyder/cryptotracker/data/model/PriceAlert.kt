package com.sneyder.cryptotracker.data.model

import android.arch.persistence.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import generateUUID

@Entity(tableName = PriceAlert.TABLE_NAME, indices = [Index(value = ["tradingPair"], unique = true)])
data class PriceAlert(
        @SerializedName("id") @Expose @PrimaryKey var id: String = generateUUID(),
        @SerializedName("tradingPair") @Expose var tradingPair: String = "",
        @SerializedName("priceBelow") @Expose var priceBelow: Double? = null,
        @SerializedName("priceAbove") @Expose var priceAbove: Double? = null,
        @SerializedName("userId") @Expose var userId: String = "",
        @Expose(serialize = false, deserialize = false) @TypeConverters(SyncStatusConverter::class) var syncStatus: SyncStatus = SyncStatus.PENDING_TO_ADD
) {

    companion object {
        const val TABLE_NAME = "PriceAlert"
    }

}