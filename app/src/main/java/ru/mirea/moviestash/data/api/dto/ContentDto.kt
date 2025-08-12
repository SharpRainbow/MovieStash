package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class ContentDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("ratingKinopoisk")
    val ratingKinopoisk: Double,
    @SerializedName("ratingImdb")
    val ratingImdb: Double,
    @SerializedName("releaseDate")
    val releaseDate: String?,
    @SerializedName("budget")
    val budget: Int,
    @SerializedName("boxOffice")
    val boxOffice: Int,
    @SerializedName("countries")
    val countries: List<CountryDto>,
    @SerializedName("genres")
    val genres: List<GenreDto>,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("description")
    val description: String,
)
