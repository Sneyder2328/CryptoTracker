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

package com.sneyder.cryptotracker.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sneyder.cryptotracker.data.api.AnnotatedConverterFactory
import com.sneyder.cryptotracker.data.api.CryptoCurrenciesApi
import com.sneyder.cryptotracker.data.api.Json
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
object RetrofitModule {

    @Provides
    @Reusable
    @JvmStatic
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
    }

    @Provides
    @Reusable
    @JvmStatic
    fun gson(): Gson = GsonBuilder()
            .setLenient()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Provides
    @Reusable
    @JvmStatic
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
            .baseUrl(CryptoCurrenciesApi.END_POINT)
            .client(okHttpClient)
            .addConverterFactory(AnnotatedConverterFactory.Builder()
                    .add(Json::class.java, GsonConverterFactory.create(gson))
                    .build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Reusable
    @JvmStatic
    fun provideCryptoCurrenciesApi(retrofit: Retrofit): CryptoCurrenciesApi = retrofit.create(CryptoCurrenciesApi::class.java)

}
