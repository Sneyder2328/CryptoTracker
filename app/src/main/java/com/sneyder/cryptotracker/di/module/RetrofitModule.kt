package com.sneyder.cryptotracker.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sneyder.cryptotracker.data.api.AnnotatedConverterFactory
import com.sneyder.cryptotracker.data.api.CryptoCurrenciesApi
import com.sneyder.cryptotracker.data.api.Json
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
    }

    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder()
            .setLenient()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
            .baseUrl(CryptoCurrenciesApi.END_POINT)
            .client(okHttpClient)
            .addConverterFactory(AnnotatedConverterFactory.Builder()
                    .add(Json::class.java, GsonConverterFactory.create(gson))
                    .build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCryptoCurrenciesApi(retrofit: Retrofit): CryptoCurrenciesApi = retrofit.create(CryptoCurrenciesApi::class.java)

}
