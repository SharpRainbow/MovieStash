package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository
import javax.inject.Inject

class GetCelebrityByIdUseCase @Inject constructor(
    private val repository: CelebrityRepository
) {

    suspend operator fun invoke(celebrityId: Int) = repository.getCelebrityById(celebrityId)
}