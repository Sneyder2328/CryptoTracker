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