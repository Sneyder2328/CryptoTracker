package com.sneyder.cryptotracker.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = CryptoCurrency.TABLE_NAME)
data class CryptoCurrency(
        @SerializedName("id") @Expose @PrimaryKey var id: Int = 0,
        @SerializedName("name") @Expose var name: String = "",
        @SerializedName("symbol") @Expose var symbol: String = "",
        @SerializedName("rank") @Expose var rankStr: String = "0",
        @SerializedName("price") @Expose var priceStr: String = "0",
        @SerializedName("volumeLast24H") @Expose var volumeLast24HStr: String = "0",
        @SerializedName("marketCap") @Expose var marketCapStr: String = "0",
        @SerializedName("availableSupply") @Expose var availableSupplyStr: String? = "0",
        @SerializedName("totalSupply") @Expose var totalSupplyStr: String? = "0",
        @SerializedName("maxSupply") @Expose var maxSupplyStr: String? = "0",
        @SerializedName("percentChangeLast1H") @Expose var percentChangeLast1HStr: String = "0",
        @SerializedName("percentChangeLast24H") @Expose var percentChangeLast24HStr: String = "0",
        @SerializedName("percentChangeLast7D") @Expose var percentChangeLast7DStr: String = "0",
        @SerializedName("lastUpdated") @Expose var lastUpdatedStr: String = "0",
        @Expose(serialize = false, deserialize = false) @Ignore var isFavorite: Boolean = false) : Parcelable {

    @Ignore
    var rank = 0
        get() = rankStr.toInt()

    @Ignore
    var price = 0.0
        get() = priceStr.toDouble()

    @Ignore
    var volumeLast24H = 0.0
        get() = volumeLast24HStr.toDouble()

    @Ignore
    var marketCap = 0L
        get() = marketCapStr.toLong()

    @Ignore
    var availableSupply: Double? = 0.0
        get() = availableSupplyStr?.toDoubleOrNull()

    @Ignore
    var totalSupply: Double? = 0.0
        get() = totalSupplyStr?.toDoubleOrNull()

    @Ignore
    var maxSupply: Double? = 0.0
        get() = maxSupplyStr?.toDoubleOrNull()

    @Ignore
    var percentChangeLast1H = 0.0f
        get() = percentChangeLast1HStr.toFloat()

    @Ignore
    var percentChangeLast24H = 0.0f
        get() = percentChangeLast24HStr.toFloat()

    @Ignore
    var percentChangeLast7D = 0.0f
        get() = percentChangeLast7DStr.toFloat()

    @Ignore
    var lastUpdated = 0L
        get() = lastUpdatedStr.toLong()

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(symbol)
        parcel.writeString(rankStr)
        parcel.writeString(priceStr)
        parcel.writeString(volumeLast24HStr)
        parcel.writeString(marketCapStr)
        parcel.writeString(availableSupplyStr)
        parcel.writeString(totalSupplyStr)
        parcel.writeString(maxSupplyStr)
        parcel.writeString(percentChangeLast1HStr)
        parcel.writeString(percentChangeLast24HStr)
        parcel.writeString(percentChangeLast7DStr)
        parcel.writeString(lastUpdatedStr)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CryptoCurrency> {
        const val TABLE_NAME = "CryptoCurrency"
        override fun createFromParcel(parcel: Parcel): CryptoCurrency {
            return CryptoCurrency(parcel)
        }

        override fun newArray(size: Int): Array<CryptoCurrency?> {
            return arrayOfNulls(size)
        }
    }

}