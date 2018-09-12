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

package com.sneyder.cryptotracker.data.api

import  com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sneyder.cryptotracker.data.model.SyncResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

class CryptoCurrenciesApiOfflineTest {

    private lateinit var restApi: CryptoCurrenciesApi
    private lateinit var mockWebServer: MockWebServer

    private fun gson(): Gson = GsonBuilder()
            .setLenient()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        val retrofit = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        restApi = retrofit.create(CryptoCurrenciesApi::class.java)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun shouldFetchCryptoCurrencies() {
        enqueueResponse("fetch_crypto_currencies.json")
        restApi.fetchCryptoCurrencies().test().apply {
            awaitCount(1)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    fun shouldAddPriceAlert() {
        enqueueResponse("add_price_alert_response.json")
        restApi.addPriceAlert("", "", "", "", "", "").test().apply {
            awaitCount(1)
            assertNoErrors()
            assertComplete()
            assertValue(SyncResponse("add", true))
        }
    }

    @Test
    fun shouldUpdatePriceAlert() {
        enqueueResponse("update_price_alert_response.json")
        restApi.updatePriceAlert("", "6000.0", "", "", "").test().apply {
            awaitCount(1)
            assertNoErrors()
            assertComplete()
            assertValue(SyncResponse("update", true))
        }
    }

    @Test
    fun shouldDeletePriceAlert() {
        enqueueResponse("delete_price_alert_response.json")
        restApi.deletePriceAlert("sdkjhgg", "", "").test().apply {
            awaitCount(1)
            assertNoErrors()
            assertComplete()
            assertValue(SyncResponse("delete", true))
        }
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String) {
        enqueueResponse(fileName, emptyMap())
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String, headers: Map<String, String>) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(mockResponse
                .setBody(source.readString(StandardCharsets.UTF_8)))
    }
}