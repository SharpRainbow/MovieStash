package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository

class GetCastByContentUseCase(
    private val repository: CelebrityRepository
) {

    suspend operator fun invoke(contentId: Int,
                                page: Int,
                                limit: Int) {
        repository.getCelebrityByContentId(
            contentId,
            page,
            limit,
            actors = true
        )
    }
}