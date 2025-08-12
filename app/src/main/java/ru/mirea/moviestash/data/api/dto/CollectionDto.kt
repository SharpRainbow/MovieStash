package ru.mirea.moviestash.data.api.dto

import com.google.gson.annotations.SerializedName

data class CollectionDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("userId")
    val userId: Int,
)

data class CreateCollectionDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
)