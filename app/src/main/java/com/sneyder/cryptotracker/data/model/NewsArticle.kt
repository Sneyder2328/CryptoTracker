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
import androidx.room.Ignore
import androidx.room.PrimaryKey
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