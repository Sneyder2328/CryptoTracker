package com.sneyder.cryptotracker.utils

import kotlin.coroutines.experimental.CoroutineContext

class AppCoroutineContextProvider: CoroutineContextProvider {
    override val Unconfined: CoroutineContext = kotlinx.coroutines.experimental.Unconfined

    override val CommonPool: CoroutineContext = kotlinx.coroutines.experimental.CommonPool

    override val UI: CoroutineContext = kotlinx.coroutines.experimental.android.UI
}