package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository

class GetPagedCrewByContentUseCase(
    private val repository: CelebrityRepository
) {

    operator fun invoke(contentId: Int)
        = repository.getCelebrityByContentIdFlow(contentId, false)
}