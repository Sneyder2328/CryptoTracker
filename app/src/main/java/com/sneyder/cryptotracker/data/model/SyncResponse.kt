package com.sneyder.cryptotracker.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SyncResponse(
        @SerializedName("typeSync") @Expose val typeSync: String,
        @SerializedName("successful") @Expose val successful: Boolean)