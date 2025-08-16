package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class ContentBaseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("releaseDate")
    val date: String?,
)
