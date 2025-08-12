package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class CredentialsDto(
    @SerializedName("login")
    val login: String,
    @SerializedName("password")
    val password: String
)