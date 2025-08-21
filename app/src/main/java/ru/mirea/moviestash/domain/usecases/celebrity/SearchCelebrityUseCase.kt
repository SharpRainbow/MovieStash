package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository
import javax.inject.Inject

class SearchCelebrityUseCase @Inject constructor(
    private val repository: CelebrityRepository
) {

    operator fun invoke(
        celebrityName: String
    ) = repository.getCelebritySearchResultFlow(
        celebrityName
    )
}