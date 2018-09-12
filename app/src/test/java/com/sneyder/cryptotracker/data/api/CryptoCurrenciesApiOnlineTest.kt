package com.sneyder.cryptotracker.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sneyder.cryptotracker.data.model.SyncResponse
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CryptoCurrenciesApiOnlineTest {

    private lateinit var restApi: CryptoCurrenciesApi

    private fun gson(): Gson = GsonBuilder()
            .setLenient()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Before
    fun setUp() {
        val retrofit = Retrofit.Builder()
                .baseUrl(CryptoCurrenciesApi.ALTERNATIVE_END_POINT)
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        restApi = retrofit.create(CryptoCurrenciesApi::class.java)
    }

    @Test
    fun shouldFetchCryptoCurrencies() {
        restApi.fetchCryptoCurrencies().test().apply {
            awaitCount(1)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    fun `should add, update and delete PriceAlert`() {
        val id = "dummyPriceAlertId"
        val userId = "adminid"
        val sessionId = "adminSessionId"
        restApi.addPriceAlert(id = id, priceBelow = "6000", priceAbove = "8000", tradingPair = "BTC/USD", userId = userId, sessionId = sessionId
        ).test().apply {
            awaitCount(1)
            assertValue(SyncResponse("add", true))
            shouldUpdatePriceAlert(id, userId, sessionId)
        }
    }

    private fun shouldUpdatePriceAlert(id: String, userId: String, sessionId: String) {
        restApi.updatePriceAlert(id, "7000", "9000", userId = userId, sessionId = sessionId).test().apply {
            awaitCount(1)
            assertValue(SyncResponse("update", true))
            shouldDeletePriceAlert(id, userId, sessionId)
        }
    }

    private fun shouldDeletePriceAlert(id: String, userId: String, sessionId: String) {
        restApi.deletePriceAlert(id = id, userId = userId, sessionId = sessionId).test().apply {
            awaitCount(1)
            assertValue(SyncResponse("delete", true))
        }
    }

    @Test
    fun `when adding PriceAlert with missing args then return false`() {
        restApi.addPriceAlert(id = "dummyPriceAlertId", sessionId = "", tradingPair = "BTC/USD", priceAbove = "", priceBelow = "7500", userId = "adminid"
        ).test().apply {
            awaitCount(1)
            assertValue(SyncResponse("add", false))
        }
    }

    @Test
    fun `when updating PriceAlert with missing args then return false`() {
        restApi.updatePriceAlert(id = "dummyPriceAlertId", priceBelow = "5000", priceAbove = "8000", userId = "adminid", sessionId = ""
        ).test().apply {
            awaitCount(1)
            assertValue(SyncResponse("update", false))
        }
    }

    @Test
    fun `when deleting PriceAlert with missing args then return false`() {
        restApi.deletePriceAlert(id = "dummyPriceAlertId", userId = "adminid", sessionId = ""
        ).test().apply {
            awaitCount(1)
            assertValue(SyncResponse("delete", false))
        }
    }
}