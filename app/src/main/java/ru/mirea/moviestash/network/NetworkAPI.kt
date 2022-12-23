package ru.mirea.moviestash.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import ru.mirea.moviestash.content.FilmRating
import ru.mirea.moviestash.search.SearchMovies

interface NetworkAPI {

    @Headers("X-API-KEY: c5a437b5-5a39-4ff2-ae3e-6347ea3711b0")
    @GET("v2.1/films/search-by-keyword")
    fun getFilm(@Query("keyword") name: String): Call<SearchMovies>

    @Headers("X-API-KEY: c5a437b5-5a39-4ff2-ae3e-6347ea3711b0")
    @GET("v2.2/films/{id}")
    fun getRating(@Path("id") id: Int): Call<FilmRating>

    @Multipart
    @Headers("Authorization: Client-ID ab08c5d0c93aa65")
    @POST("/3/image")
    fun postImage(@Part image: MultipartBody.Part): Call<ImgurResponse>

}