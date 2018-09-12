package com.sneyder.cryptotracker.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = GlobalData.TABLE_NAME)
data class GlobalData(
        @SerializedName("id") @Expose @PrimaryKey var id: String = "",
        @SerializedName("totalMarketCapUsd") @Expose var totalMarketCapUsd: String,
        @SerializedName("total24HVolumeUsd") @Expose var total24HVolumeUsd: String,
        @SerializedName("bitcoinPercentageOfMarketCap") @Expose var bitcoinPercentageOfMarketCap: String,
        @SerializedName("activeCurrencies") @Expose var activeCurrencies: String,
        @SerializedName("activeMarkets") @Expose var activeMarkets: String,
        @SerializedName("lastUpdated") @Expose var lastUpdated: String) {

    companion object {
        const val TABLE_NAME = "GlobalData"
    }

}