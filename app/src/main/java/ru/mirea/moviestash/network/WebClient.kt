package ru.mirea.moviestash.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WebClient {
    private val kinopoisk: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://kinopoiskapiunofficial.tech/api/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private val imgur: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.imgur.com")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val kinopoiskAPI: NetworkAPI by lazy {
        kinopoisk.create(NetworkAPI::class.java)
    }

    val imgurAPI: NetworkAPI by lazy {
        imgur.create(NetworkAPI::class.java)
    }
}