package com.sneyder.cryptotracker.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = User.TABLE_NAME)
data class User(
        @SerializedName("userId") @Expose @PrimaryKey val userId: String,
        @SerializedName("displayName") @Expose val displayName: String,
        @SerializedName("sessionId") @Expose var sessionId: String = "",
        @SerializedName("typeUser") @Expose var typeUser: String = "",
        @SerializedName("firebaseTokenId") @Expose val firebaseTokenId: String=""
) {
    companion object {
        const val TABLE_NAME = "User"
    }
}