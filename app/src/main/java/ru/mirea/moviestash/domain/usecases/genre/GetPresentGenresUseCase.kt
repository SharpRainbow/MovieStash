package ru.mirea.moviestash.domain.usecases.genre

import ru.mirea.moviestash.domain.GenreRepository
import javax.inject.Inject

class GetPresentGenresUseCase @Inject constructor(
    private val repository: GenreRepository
) {

    suspend operator fun invoke() = repository.getPresentGenres()
}