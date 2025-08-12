package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class CelebrityInContentDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("role")
    val role: String,
)