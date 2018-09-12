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

enum class SortCurrenciesBy(val code: Int) {
    MCAP_D(0), NAME_D(1), PRICE_D(2), CHANGE_D(3), VOL_D(4),
    MCAP_A(5), NAME_A(6), PRICE_A(7), CHANGE_A(8), VOL_A(9)
}

fun Int.toSortCurrenciesBy(): SortCurrenciesBy {
    return when(this){
        0 -> SortCurrenciesBy.MCAP_D
        1 -> SortCurrenciesBy.NAME_D
        2 -> SortCurrenciesBy.PRICE_D
        3 -> SortCurrenciesBy.CHANGE_D
        4 -> SortCurrenciesBy.VOL_D

        5 -> SortCurrenciesBy.MCAP_A
        6 -> SortCurrenciesBy.NAME_A
        7 -> SortCurrenciesBy.PRICE_A
        8 -> SortCurrenciesBy.CHANGE_A
        9 -> SortCurrenciesBy.VOL_A

        else -> throw Exception("There is no SortCurrenciesBy value for Int = $this in function toSortCurrenciesBy()")
    }
}