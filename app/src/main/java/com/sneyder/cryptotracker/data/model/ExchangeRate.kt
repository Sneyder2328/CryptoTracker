package com.sneyder.cryptotracker.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = ExchangeRate.TABLE_NAME)
data class ExchangeRate(
        @SerializedName("symbol") @Expose @PrimaryKey var symbol: String,
        @SerializedName("rate") @Expose val rate: Double){

    companion object {
        const val TABLE_NAME = "ExchangeRate"
    }

}
