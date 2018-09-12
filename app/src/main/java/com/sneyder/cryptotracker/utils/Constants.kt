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

package com.sneyder.cryptotracker.utils


const val DEVELOPER_EMAIL = "twismartinc@gmail.com"


infix fun Double.multiply(otherNumber: Double) = this.toBigDecimal().multiply(otherNumber.toBigDecimal()).toDouble()
infix fun Double.multiply(otherNumber: Float) = this.toBigDecimal().multiply(otherNumber.toBigDecimal()).toDouble()
infix fun Float.multiply(otherNumber: Double) = this.toBigDecimal().multiply(otherNumber.toBigDecimal()).toDouble()
infix fun Float.multiply(otherNumber: Float) = this.toBigDecimal().multiply(otherNumber.toBigDecimal()).toDouble()

infix fun Double.divide(otherNumber: Double) = this.toBigDecimal().divide(otherNumber.toBigDecimal()).toDouble()
infix fun Double.divide(otherNumber: Float) = this.toBigDecimal().divide(otherNumber.toBigDecimal()).toDouble()
infix fun Float.divide(otherNumber: Double) = this.toBigDecimal().divide(otherNumber.toBigDecimal()).toDouble()
infix fun Float.divide(otherNumber: Float) = this.toBigDecimal().divide(otherNumber.toBigDecimal()).toDouble()

fun getImgUrlForCryptoSymbol(symbol: String) = "https://images.coinviewer.io/currencies/128x128/$symbol.png"
fun getImgUrlForCryptoId(coinId: Int) = "https://s2.coinmarketcap.com/static/img/coins/128x128/$coinId.png"

class Constants {
    companion object {
        val CURRENCIES =
                arrayOf(
                        "USD",
                        "BTC",
                        "ETH",
                        "AUD",
                        "BRL",
                        "CAD",
                        "CHF",
                        "CLP",
                        "CNY",
                        "CZK",
                        "DKK",
                        "EUR",
                        "GBP",
                        "HKD",
                        "HUF",
                        "IDR",
                        "ILS",
                        "JPY",
                        "KRW",
                        "MXN",
                        "MYR",
                        "NOK",
                        "NZD",
                        "PHP",
                        "PKR",
                        "PLN",
                        "RUB",
                        "SEK",
                        "SGD",
                        "THB",
                        "TRY",
                        "TWD",
                        "ZAR"
                )

        val CURRENCIES_SYMBOL: Map<String, String> = mapOf(
                "USD" to "$",
                "BTC" to "Ƀ",
                "ETH" to "Ξ",
                "AUD" to "$",
                "BRL" to "R$",
                "CAD" to "$",
                "CHF" to "Fr.",
                "CLP" to "$",
                "CNY" to "¥",
                "CZK" to "Kč",
                "DKK" to "Kr",
                "EUR" to "€",
                "GBP" to "£",
                "HKD" to "元",
                "HUF" to "Ft",
                "IDR" to "Rp",
                "ILS" to "₪",
                "JPY" to "¥",
                "KRW" to "₩",
                "MXN" to "$",
                "MYR" to "RM",
                "NOK" to "kr",
                "NZD" to "$",
                "PHP" to "₱",
                "PKR" to "Rs",
                "PLN" to "zł",
                "RUB" to "\u20BD",
                "SEK" to "kr",
                "SGD" to "$",
                "THB" to "฿",
                "TRY" to "₺",
                "TWD" to "NT$",
                "ZAR" to "R"
        )

    }

}