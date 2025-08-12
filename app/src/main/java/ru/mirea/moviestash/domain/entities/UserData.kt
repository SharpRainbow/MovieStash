package ru.mirea.moviestash.domain.entities

data class UserData(
    val userId: Int,
    val role: Role
)

enum class Role {
    USER,
    MODERATOR,
}