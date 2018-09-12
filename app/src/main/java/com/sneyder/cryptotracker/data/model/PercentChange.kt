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