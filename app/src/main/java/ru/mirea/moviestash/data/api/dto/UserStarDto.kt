package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class UserStarDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("contentId")
    val contentId: Int,
    @SerializedName("userId")
    val userId: Int
)

data class AddUserStarDto(
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("contentId")
    val contentId: Int,
)