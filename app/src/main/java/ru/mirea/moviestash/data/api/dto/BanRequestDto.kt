package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class BanRequestDto(
    @SerializedName("banReason")
    val banReason: String,
)