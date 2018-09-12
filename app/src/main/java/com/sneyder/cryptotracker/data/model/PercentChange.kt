package com.sneyder.cryptotracker.data.model

enum class PercentChange(val code: Int) {
    LAST_1_H(0), LAST_24_H(1), LAST_7_D(2)
}

fun Int.toPercentChange(): PercentChange {
    return when (this) {
        0 -> PercentChange.LAST_1_H
        1 -> PercentChange.LAST_24_H
        2 -> PercentChange.LAST_7_D
        else -> throw Exception("There is no PercentChange value for Int = $this in function toPercentChange()")
    }
}

fun PercentChange.from(cryptoCurrency: CryptoCurrency): Float {
    return when(this){
        PercentChange.LAST_1_H -> cryptoCurrency.percentChangeLast1H
        PercentChange.LAST_24_H -> cryptoCurrency.percentChangeLast24H
        PercentChange.LAST_7_D -> cryptoCurrency.percentChangeLast7D
    }
}