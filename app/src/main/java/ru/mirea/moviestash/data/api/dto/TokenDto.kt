package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class TokenDto(
    @SerializedName("token")
    val token: String
)