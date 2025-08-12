package ru.mirea.moviestash.domain.entities

data class UserEntity(
    val id: Int,
    val login: String,
    val nickname: String,
    val email: String,
    val isBanned: Boolean,
    val banDate: String,
    val banReason: String
)
