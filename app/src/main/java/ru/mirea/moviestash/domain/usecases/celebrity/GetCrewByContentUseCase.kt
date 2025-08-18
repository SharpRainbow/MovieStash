package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository

class GetCrewByContentUseCase(
    private val repository: CelebrityRepository
) {

    suspend operator fun invoke(contentId: Int, limit: Int) =
        repository.getFirstNCelebrityByContentId(
            contentId,
            limit,
            actors = false
        )
}