package ru.mirea.moviestash.domain.entities

data class NewsEntity(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val date: String,
)
