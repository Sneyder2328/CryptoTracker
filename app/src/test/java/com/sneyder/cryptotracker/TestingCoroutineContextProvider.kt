package com.sneyder.cryptotracker

import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import kotlin.coroutines.experimental.CoroutineContext

class TestingCoroutineContextProvider : CoroutineContextProvider {

    override val Unconfined: CoroutineContext = kotlinx.coroutines.experimental.Unconfined
    override val CommonPool: CoroutineContext = kotlinx.coroutines.experimental.Unconfined
    override val UI: CoroutineContext = kotlinx.coroutines.experimental.Unconfined
}