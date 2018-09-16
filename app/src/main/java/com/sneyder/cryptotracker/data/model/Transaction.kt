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

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sneyder.cryptotracker.utils.divide
import com.sneyder.cryptotracker.utils.multiply
import generateUUID
import java.math.BigDecimal

@Entity(tableName = Transaction.TABLE_NAME)
data class Transaction(
        @SerializedName("id") @Expose @PrimaryKey var id: String = generateUUID(),
        @SerializedName("userId") @Expose var userId: String = "",
        @SerializedName("tradingPair") @Expose var tradingPair: String = "",
        @SerializedName("quantity") @Expose var quantity: Double = 0.0,
        @SerializedName("price") @Expose var price: Double = 0.0,
        @SerializedName("date") @Expose var date: Long = 0,
        @SerializedName("fee") @Expose var fee: Float = 0.0f,
        @Expose(serialize = false, deserialize = false) @TypeConverters(SyncStatusConverter::class) var syncStatus: SyncStatus = SyncStatus.PENDING_TO_ADD
) : Parcelable {

    @Ignore
    fun isABuy() = quantity >= 0

    @Ignore
    fun cryptoSymbol() = tradingPair.substring(0, tradingPair.indexOf("/"))

    @Ignore
    fun fiatSymbol() = tradingPair.substring(tradingPair.indexOf("/") + 1)

    @Ignore
    fun quantityMinusFee(): Double {
        return if (isABuy()) {
            quantity multiply (1 - (fee divide 100.0))
        } else quantity multiply (1 + (fee divide 100.0))
    }

    @Ignore
    fun totalCost(): Double {
        return Math.abs(BigDecimal(quantity).multiply(BigDecimal(price)).toDouble())
    }

    @Ignore
    fun getProfits(currentPrice: Double): Double {
        return if (isABuy()) {
            Math.abs(currentPrice * quantityMinusFee()) - totalCost()
        } else {
            totalCost() - Math.abs(currentPrice * quantityMinusFee())
        }
    }

    @Ignore
    fun getProfitsPercentage(currentPrice: Double): Double {
        return if (isABuy()) {
            val currentValue = Math.abs(currentPrice * quantityMinusFee())
            val p3 = currentValue - totalCost()
            p3 / currentValue * 100
        } else {
            val currentValue = Math.abs(currentPrice * quantityMinusFee())
            val p3 = totalCost() - currentValue
            p3 / currentValue * 100
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readLong(),
            parcel.readFloat())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(tradingPair)
        parcel.writeDouble(quantity)
        parcel.writeDouble(price)
        parcel.writeLong(date)
        parcel.writeFloat(fee)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        const val TABLE_NAME = "TransactionTable"
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }
}