package ru.mirea.moviestash.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.domain.GenreRepository
import ru.mirea.moviestash.domain.entities.GenreEntity

class GenreRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : GenreRepository {

    override suspend fun getPresentGenres(): Result<List<GenreEntity>> {
        return try {
            Log.d("GenreRepositoryImpl", "Fetching present genres")
            val result = movieStashApi.getPresentGenres()
            Result.Success(result.toListEntity())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGenreById(genreId: Int): Result<GenreEntity> {
        return try {
            val result = movieStashApi.getGenreById(genreId)
            Result.Success(result.toEntity())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}