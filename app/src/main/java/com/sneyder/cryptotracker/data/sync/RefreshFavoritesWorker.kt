package com.sneyder.cryptotracker.data.sync

import androidx.work.Worker
import com.sneyder.cryptotracker.BaseApp
import debug
import javax.inject.Inject

class RefreshFavoritesWorker: Worker() {

    @Inject lateinit var favoritesSynchronizer: FavoritesSynchronizer

    override fun doWork(): Result {
        debug("RefreshFavoritesWorker doWork START")
        try {
            (applicationContext as BaseApp).appComponent.inject(this)
            favoritesSynchronizer.refreshFavorites()
        } catch (e: Exception) {
            e.printStackTrace()
            debug("RefreshFavoritesWorker RETRY")
            return Result.RETRY
        }
        debug("RefreshFavoritesWorker doWork SUCCESS")
        return Result.SUCCESS
    }
}