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

package com.sneyder.cryptotracker.utils

import kotlin.coroutines.experimental.CoroutineContext

class AppCoroutineContextProvider: CoroutineContextProvider {
    override val Unconfined: CoroutineContext = kotlinx.coroutines.experimental.Unconfined

    override val CommonPool: CoroutineContext = kotlinx.coroutines.experimental.CommonPool

    override val UI: CoroutineContext = kotlinx.coroutines.experimental.android.UI
}