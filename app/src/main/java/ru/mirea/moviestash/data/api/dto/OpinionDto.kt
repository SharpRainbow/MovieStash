package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class OpinionDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
