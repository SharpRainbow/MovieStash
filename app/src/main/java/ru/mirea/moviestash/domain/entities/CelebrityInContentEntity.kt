package ru.mirea.moviestash.domain.entities

data class CelebrityInContentEntity(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val role: String,
)