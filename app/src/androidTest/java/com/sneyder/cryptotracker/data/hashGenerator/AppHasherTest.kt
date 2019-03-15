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

package com.sneyder.cryptotracker.data.hashGenerator

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppHasherTest {

    private lateinit var h: Hasher

    @Before
    fun setUp() {
        h = AppHasher()
    }

    @Test
    fun hashIsTheSame() {
        runBlocking {
            Assert.assertEquals(h.hash("sneyder"), h.hash("sneyder"))
        }
    }

    @Test
    fun hashDoesNotContainSpaces(){
        runBlocking {
            Assert.assertFalse(h.hash("u").contains(' '))
        }
    }
}