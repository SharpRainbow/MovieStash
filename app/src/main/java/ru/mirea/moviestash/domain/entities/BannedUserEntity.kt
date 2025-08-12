package ru.mirea.moviestash.domain.entities

data class BannedUserEntity(
    val id: Int,
    val email: String,
    val nickname: String,
    val banDate: String,
    val banReason: String
)