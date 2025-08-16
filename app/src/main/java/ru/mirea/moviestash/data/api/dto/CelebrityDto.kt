package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class CelebrityDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("birthday")
    val birthDate: String?,
    @SerializedName("death")
    val death: String?,
    @SerializedName("birthplace")
    val birthPlace: String,
    @SerializedName("career")
    val career: String,
    @SerializedName("height")
    val height: Int
)