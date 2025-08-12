package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class NewsDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("date")
    val date: String,
)