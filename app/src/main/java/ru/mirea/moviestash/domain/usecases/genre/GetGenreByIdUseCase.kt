package ru.mirea.moviestash.domain.usecases.genre

import ru.mirea.moviestash.domain.GenreRepository

class GetGenreByIdUseCase(
    private val repository: GenreRepository
) {

    suspend operator fun invoke(genreId: Int) =  repository.getGenreById(genreId)
}