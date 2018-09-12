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

import android.util.Base64
import debug
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AppHasher : Hasher() {

    override fun getPepper(): String = "amsjjftyrooowmssneydfezhoarsss455s"


    override suspend fun hash(
            plainText: String,
            keyLength: Int, // 256-bits for AES-256, 128-bits for AES-128, etc
            iterationCount: Int
    ): String {
        return withContext(CommonPool) {
            debug("starting at: ${System.currentTimeMillis()}")
            val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val pepper: ByteArray = Base64.decode("imfa${getPepper()}", Base64.DEFAULT)
            val keySpec = PBEKeySpec(plainText.toCharArray(), pepper, iterationCount, keyLength)
            val keyBytes = keyFactory.generateSecret(keySpec).encoded
            val hashed = Base64.encodeToString(SecretKeySpec(keyBytes, "AES").encoded, Base64.DEFAULT)
            debug("ending at: ${System.currentTimeMillis()} hashed = $hashed")
            hashed.trim()
        }
    }
}