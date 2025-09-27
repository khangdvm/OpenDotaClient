package com.example.opendotaclient.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // quan trọng!
        .build()

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.opendota.com/api/")
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // quan trọng!
            .client(client)
            .build()
    }

    val api: OpenDotaService by lazy { retrofit.create(OpenDotaService::class.java) }
}
