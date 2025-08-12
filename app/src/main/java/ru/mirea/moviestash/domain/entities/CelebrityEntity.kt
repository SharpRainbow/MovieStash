package ru.mirea.moviestash.domain.entities

data class CelebrityEntity(
    val id: Int,
    val name: String,
    val image: String,
    val birthDate: String,
    val death: String,
    val birthPlace: String,
    val career: String,
    val height: Int
)

data class CelebrityEntityBase(
    val id: Int,
    val name: String,
    val image: String,
    val birthDate: String,
)