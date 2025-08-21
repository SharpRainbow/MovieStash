package ru.mirea.moviestash.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    val movieStashApi: MovieStashApi by lazy {
        retrofit.create(MovieStashApi::class.java)
    }

    const val FIRST_PAGE_INDEX = 1
    const val NETWORK_PAGE_SIZE = 10
    const val BASE_URL = "http://192.168.0.122:8080/api/v1/"
}