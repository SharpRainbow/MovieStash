package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.domain.entities.GenreEntity

interface GenreRepository {

    suspend fun getPresentGenres(): List<GenreEntity>

    suspend fun getGenreById(genreId: Int): GenreEntity

}