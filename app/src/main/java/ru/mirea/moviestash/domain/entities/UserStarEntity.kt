package ru.mirea.moviestash.domain.entities

data class UserStarEntity(
    val id: Int,
    val rating: Int,
    val contentId: Int,
    val userId: Int
)
