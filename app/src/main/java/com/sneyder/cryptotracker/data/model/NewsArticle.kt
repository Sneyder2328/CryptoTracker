package com.sneyder.cryptotracker.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = NewsArticle.TABLE_NAME)
data class NewsArticle(
        @SerializedName("title") @Expose var title: String,
        @SerializedName("link") @Expose @PrimaryKey var link: String,
        @SerializedName("imgUrl") @Expose var imgUrl: String = "",
        @SerializedName("pubDate") @Expose var pubDate: String = ""
){

    @Ignore
    fun outlet(): String {
        return when{
            link.contains("bitcoinist.com") -> "Bitcoinist"
            link.contains("cointelegraph.com") -> "CoinTelegraph"
            link.contains("newsbtc.com") -> "NewsBTC"
            link.contains("coindesk.com") -> "CoinDesk"
            link.contains("ccn.com") -> "CCN"
            link.contains("nulltx.com") -> "NullTX"
            link.contains("cryptoglobe.com") -> "CryptoGlobe"
            link.contains("news.bitcoin") -> "Bitcoin.com"
            else -> ""
        }
    }
    companion object {
        const val TABLE_NAME = "NewsArticle"
    }
}