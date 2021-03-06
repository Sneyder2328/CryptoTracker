/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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