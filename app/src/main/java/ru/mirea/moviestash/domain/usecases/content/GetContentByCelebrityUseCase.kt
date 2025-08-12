package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class GetContentByCelebrityUseCase(
    private val repository: ContentRepository
) {

    suspend operator fun invoke(
        celebrityId: Int,
        page: Int,
        limit: Int
    ) = repository.getContentByCelebrity(celebrityId, page, limit)
}