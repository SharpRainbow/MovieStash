package ru.mirea.moviestash.domain.entities

data class ContentEntity(
    val id: Int,
    val name: String,
    val image: String,
    val rating: Double,
    val ratingKinopoisk: Double,
    val ratingImdb: Double,
    val releaseDate: String,
    val budget: Int,
    val boxOffice: Int,
    val countries: String,
    val genres: String,
    val duration: String,
    val description: String,
)

data class ContentEntityBase(
    val id: Int,
    val name: String,
    val image: String,
    val rating: Double,
    val releaseDate: String,
)