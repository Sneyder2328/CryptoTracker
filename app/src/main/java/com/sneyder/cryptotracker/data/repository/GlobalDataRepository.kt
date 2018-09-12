package com.sneyder.cryptotracker.data.repository

import com.sneyder.cryptotracker.data.model.GlobalData
import io.reactivex.Completable
import io.reactivex.Flowable

abstract class GlobalDataRepository {

    abstract fun findGlobalData(): Flowable<GlobalData>

    abstract fun refreshGlobalData(): Completable

}