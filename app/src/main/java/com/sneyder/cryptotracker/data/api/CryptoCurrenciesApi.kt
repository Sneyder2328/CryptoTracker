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

import com.sneyder.cryptotracker.data.model.*
import io.reactivex.Single
import retrofit2.http.*



interface CryptoCurrenciesApi {

    companion object {

        const val END_POINT = "https://sneyder.net/CryptoTracker/"

        const val ALTERNATIVE_END_POINT = "http://52.21.172.205/CryptoTracker/"

        const val SIGN_UP_USER = "signUp.php"

        const val LOG_IN_USER = "logIn.php"

        const val FETCH_CRYPTO_CURRENCIES = "fetchCryptoCurrencies.php"

        const val FETCH_GLOBAL_DATA = "fetchGlobalData.php"

        const val ADD_PRICE_ALERT = "addPriceAlert.php"

        const val ADD_FAVORITE = "addFavorite.php"

        const val ADD_TRANSACTION = "addTransaction.php"

        const val DELETE_FAVORITE = "deleteFavorite.php"

        const val DELETE_TRANSACTION = "deleteTransaction.php"

        const val DELETE_PRICE_ALERT = "deletePriceAlert.php"

        const val UPDATE_PRICE_ALERT = "updatePriceAlert.php"

        const val UPDATE_TRANSACTION = "updateTransaction.php"

        const val UPDATE_FIREBASE_TOKEN_ID = "updateFirebaseTokenId.php"

        const val GET_PRICE_ALERTS_BY_USER_ID = "getPriceAlertsByUserId.php"

        const val GET_FAVORITES_BY_USER_ID = "getFavoritesByUserId.php"

        const val GET_TRANSACTIONS_BY_USER_ID = "getTransactionsByUserId.php"

        const val GET_EXCHANGE_RATES = "getExchangeRates.php"

        const val GET_NEWS_ARTICLES = "getNewsArticles.php"

        const val LOG_OUT = "logOut.php"

    }

    @POST(SIGN_UP_USER) @Json
    @FormUrlEncoded
    fun signUpUser(
            @Field("userId") userId: String,
            @Field("displayName") displayName: String,
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("typeLogin") typeLogin: String,
            @Field("firebaseTokenId") firebaseTokenId: String,
            @Field("accessToken") accessToken: String
    ): Single<User>

    @POST(LOG_IN_USER) @Json
    @FormUrlEncoded
    fun logInUser(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("typeLogin") typeLogin: String,
            @Field("accessToken") accessToken: String = "",
            @Field("userId") userId: String = ""
    ): Single<User>

    @POST(UPDATE_FIREBASE_TOKEN_ID) @Json
    @FormUrlEncoded
    fun updateFirebaseTokenId(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("firebaseTokenId") firebaseTokenId: String
    ): Single<String>

    @GET @Json
    fun fetchCryptoCurrencies(
            @Url url: String = FETCH_CRYPTO_CURRENCIES,
            @Query("currencies") currencies: String = "allCurrencies"
    ): Single<List<CryptoCurrency>>

    @GET @Json
    fun getFavoritesByUserId(
            @Url url: String = GET_FAVORITES_BY_USER_ID,
            @Query("userId") userId: String,
            @Query("sessionId") sessionId: String
    ): Single<List<Favorite>>

    @GET @Json
    fun getTransactionsByUserId(
            @Url url: String = GET_TRANSACTIONS_BY_USER_ID,
            @Query("userId") userId: String,
            @Query("sessionId") sessionId: String
    ): Single<List<Transaction>>

    @GET @Json
    fun getPriceAlertsByUserId(
            @Url url: String = GET_PRICE_ALERTS_BY_USER_ID,
            @Query("userId") userId: String,
            @Query("sessionId") sessionId: String
    ): Single<List<PriceAlert>>

    @GET @Json
    fun fetchGlobalData(
            @Url url: String = FETCH_GLOBAL_DATA
    ): Single<GlobalData>

    @POST(ADD_PRICE_ALERT) @Json
    @FormUrlEncoded
    fun addPriceAlert(
            @Field("id") id: String,
            @Field("sessionId") sessionId: String,
            @Field("tradingPair") tradingPair: String,
            @Field("priceBelow") priceBelow: String,
            @Field("priceAbove") priceAbove: String,
            @Field("userId") userId: String = ""
    ): Single<SyncResponse>

    @POST(ADD_TRANSACTION) @Json
    @FormUrlEncoded
    fun addTransaction(
            @Field("id") id: String,
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("tradingPair") tradingPair: String,
            @Field("quantity") quantity: String,
            @Field("price") price: String,
            @Field("date") date: String,
            @Field("fee") fee: String
    ): Single<SyncResponse>

    @POST(ADD_FAVORITE) @Json
    @FormUrlEncoded
    fun addFavorite(
            @Field("userId") userId: String,
            @Field("currencyId") currencyId: String,
            @Field("sessionId") sessionId: String
    ): Single<SyncResponse>

    @POST(UPDATE_PRICE_ALERT) @Json
    @FormUrlEncoded
    fun updatePriceAlert(
            @Field("id") id: String,
            @Field("priceBelow") priceBelow: String,
            @Field("priceAbove") priceAbove: String,
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String
    ): Single<SyncResponse>

    @POST(UPDATE_TRANSACTION) @Json
    @FormUrlEncoded
    fun updateTransaction(
            @Field("id") id: String,
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("tradingPair") tradingPair: String,
            @Field("quantity") quantity: String,
            @Field("price") price: String,
            @Field("date") date: String,
            @Field("fee") fee: String
    ): Single<SyncResponse>

    @POST(DELETE_PRICE_ALERT) @Json
    @FormUrlEncoded
    fun deletePriceAlert(
            @Field("id") id: String,
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String
    ): Single<SyncResponse>

    @POST(DELETE_TRANSACTION) @Json
    @FormUrlEncoded
    fun deleteTransaction(
            @Field("id") id: String,
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String
    ): Single<SyncResponse>

    @POST(DELETE_FAVORITE) @Json
    @FormUrlEncoded
    fun deleteFavorite(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String,
            @Field("currencyId") currencyId: String
    ): Single<SyncResponse>

    @GET @Json
    fun getExchangeRates(
            @Url url: String = GET_EXCHANGE_RATES
    ): Single<List<ExchangeRate>>

    @GET @Json
    fun getNewsArticles(
            @Url url: String = GET_NEWS_ARTICLES
    ): Single<List<NewsArticle>>

    @POST(LOG_OUT) @Json
    @FormUrlEncoded
    fun logOut(
            @Field("userId") userId: String,
            @Field("sessionId") sessionId: String
    ): Single<String>

}