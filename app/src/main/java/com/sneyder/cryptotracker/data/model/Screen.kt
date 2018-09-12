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

import com.sneyder.cryptotracker.R

enum class Screen(val value: Int) {
    ALL_COINS(0), FAVORITES(1), PORTFOLIO(2), NEWS(3), GLOBAL_DATA(4);
}
fun Int.toScreen(): Screen {
    return when(this){
        0, R.id.nav_all_coins -> Screen.ALL_COINS
        1, R.id.nav_favorite_coins -> Screen.FAVORITES
        2, R.id.nav_portfolio -> Screen.PORTFOLIO
        3, R.id.nav_news -> Screen.NEWS
        4, R.id.nav_global_data -> Screen.GLOBAL_DATA
        else -> throw Exception("$this value not supported in function toScreen")
    }
}