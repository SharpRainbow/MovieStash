package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

class RegisterDto(
    @SerializedName("login")
    val login: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)