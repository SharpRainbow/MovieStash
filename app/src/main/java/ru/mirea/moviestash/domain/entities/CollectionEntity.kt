package ru.mirea.moviestash.domain.entities

data class CollectionEntity(
    val id: Int,
    val name: String,
    val description: String,
    val userId: Int,
)