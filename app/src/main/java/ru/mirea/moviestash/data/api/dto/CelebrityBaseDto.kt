package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class CelebrityBaseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("date")
    val birthDate: String,
)