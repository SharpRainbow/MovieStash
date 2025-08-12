package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository

class GetCelebrityByIdUseCase(
    private val repository: CelebrityRepository
) {

    suspend operator fun invoke(celebrityId: Int) = repository.getCelebrityById(celebrityId)
}