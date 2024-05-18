package ru.mirea.moviestash.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mirea.moviestash.AppConstants
import ru.mirea.moviestash.content.FilmRating
import ru.mirea.moviestash.search.SearchMovies

interface NetworkAPI {

    @Headers("X-API-KEY: ${AppConstants.kpApiKey}")
    @GET("v2.1/films/search-by-keyword")
    fun getFilm(@Query("keyword") name: String): Call<SearchMovies>

    @Headers("X-API-KEY: ${AppConstants.kpApiKey}")
    @GET("v2.2/films/{id}")
    fun getRating(@Path("id") id: Int): Call<FilmRating>

    @Multipart
    @Headers("Authorization: Client-ID ${AppConstants.imgurApiKey}")
    @POST("/3/image")
    fun postImage(@Part image: MultipartBody.Part): Call<ImgurResponse>

}