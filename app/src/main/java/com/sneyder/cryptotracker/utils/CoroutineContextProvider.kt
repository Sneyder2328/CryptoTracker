package com.sneyder.cryptotracker.utils

import kotlin.coroutines.experimental.CoroutineContext

@SuppressWarnings()
interface CoroutineContextProvider {
    val Unconfined: CoroutineContext

    val CommonPool: CoroutineContext

    val UI: CoroutineContext
}