package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class BannedUserDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("banDate")
    val banDate: String?,
    @SerializedName("banReason")
    val banReason: String?
)