package ru.mirea.moviestash.domain.usecases.genre

import ru.mirea.moviestash.domain.GenreRepository

class GetPresentGenresUseCase(
    private val repository: GenreRepository
) {

    suspend operator fun invoke() = repository.getPresentGenres()
}