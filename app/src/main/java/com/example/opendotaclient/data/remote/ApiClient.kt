package com.example.opendotaclient.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.opendota.com/api/"

    val api: OpenDotaApi by lazy {
        // logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // http client
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        // retrofit
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(OpenDotaApi::class.java)
    }
}
