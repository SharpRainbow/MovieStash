package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("opinion")
    val opinion: OpinionDto,
    @SerializedName("contentId")
    val contentId:  Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userName")
    val userName: String,
)

data class AddReviewDto(
    @SerializedName("opinionId")
    val opinionId: Int,
    @SerializedName("contentId")
    val contentId:  Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
)

data class UpdateReviewDto(
    @SerializedName("opinionId")
    val opinionId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
)