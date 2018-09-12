package com.sneyder.cryptotracker.data.model

import android.os.Parcel
import android.os.Parcelable

data class TransactionsByPairSet(
        var tradingPair: String = "BTC/USD",
        var quantity: Double = 0.0,
        var quantityWithFees: Double = 0.0,
        var amount: Double = 0.0,
        var currentPrice: Double = 0.0,
        var rateToUsd: Double = 0.0) : Parcelable {

    var currentValueInUsd: Double = 0.0
        get() = currentPrice * quantity / rateToUsd

    var profits: Double = 0.0
        get() = (currentValueInUsd * rateToUsd) - amount

    var cryptoSymbol = ""
        get() = tradingPair.substring(0, tradingPair.indexOf("/"))

    var fiatSymbol = ""
        get() = tradingPair.substring(tradingPair.indexOf("/")+1)

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tradingPair)
        parcel.writeDouble(quantity)
        parcel.writeDouble(amount)
        parcel.writeDouble(currentPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransactionsByPairSet> {
        override fun createFromParcel(parcel: Parcel): TransactionsByPairSet {
            return TransactionsByPairSet(parcel)
        }

        override fun newArray(size: Int): Array<TransactionsByPairSet?> {
            return arrayOfNulls(size)
        }
    }

}