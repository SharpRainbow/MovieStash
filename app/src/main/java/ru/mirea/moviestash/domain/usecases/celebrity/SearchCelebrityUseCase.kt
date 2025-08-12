package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository

class SearchCelebrityUseCase(
    private val repository: CelebrityRepository
) {

    suspend operator fun invoke(
        celebrityName: String,
        page: Int,
        limit: Int
    ) = repository.searchForCelebrity(
        celebrityName, page, limit
    )
}