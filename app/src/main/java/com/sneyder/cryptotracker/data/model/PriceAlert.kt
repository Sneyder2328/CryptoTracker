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

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
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