package ru.mirea.moviestash.domain.entities

data class ReviewEntity(
    val id: Int,
    val opinion: String,
    val contentId: Int,
    val date: String,
    val title: String,
    val description: String,
    val userId: Int,
    val userName: String,
)