package ru.mirea.moviestash.domain

import ru.mirea.moviestash.domain.entities.GenreEntity

interface GenreRepository {

    suspend fun getPresentGenres(): List<GenreEntity>

    suspend fun getGenreById(genreId: Int): GenreEntity

}