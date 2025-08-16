package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val login: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("isBanned")
    val isBanned: Boolean,
    @SerializedName("banDate")
    val banDate: String?,
    @SerializedName("banReason")
    val banReason: String
)

data class UpdateUserDto(
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String?
)