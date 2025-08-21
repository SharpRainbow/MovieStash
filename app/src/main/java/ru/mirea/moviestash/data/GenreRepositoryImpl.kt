package ru.mirea.moviestash.data

import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.GenreRepository
import ru.mirea.moviestash.domain.entities.GenreEntity
import javax.inject.Inject

@ApplicationScope
class GenreRepositoryImpl @Inject constructor(
    private val movieStashApi: MovieStashApi
) : GenreRepository {

    override suspend fun getPresentGenres(): List<GenreEntity> {
        return movieStashApi.getPresentGenres().toListEntity()
    }

    override suspend fun getGenreById(genreId: Int): GenreEntity {
        return movieStashApi.getGenreById(genreId).toEntity()
    }
}