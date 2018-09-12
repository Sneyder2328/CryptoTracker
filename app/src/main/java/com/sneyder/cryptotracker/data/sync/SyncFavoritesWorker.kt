package com.sneyder.cryptotracker.data.sync

import androidx.work.Worker
import com.sneyder.cryptotracker.BaseApp
import debug
import javax.inject.Inject

class SyncFavoritesWorker: Worker() {

    @Inject lateinit var favoritesSynchronizer: FavoritesSynchronizer

    override fun doWork(): Result {
        debug("SyncFavoritesWorker doWork START")
        try {
            (applicationContext as BaseApp).appComponent.inject(this)
            favoritesSynchronizer.synchronizeFavoritesWithServer()
        } catch (e: Exception) {
            e.printStackTrace()
            debug("SyncFavoritesWorker RETRY")
            return Result.RETRY
        }
        debug("SyncFavoritesWorker doWork SUCCESS")
        return Result.SUCCESS
    }
}