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

package com.sneyder.cryptotracker.ui.base

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.sneyder.cryptotracker.RxImmediateSchedulerRule
import org.junit.Rule

open class BaseViewModelTest {

    @Rule @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    @Rule @JvmField val archComponentsRule = InstantTaskExecutorRule()
}