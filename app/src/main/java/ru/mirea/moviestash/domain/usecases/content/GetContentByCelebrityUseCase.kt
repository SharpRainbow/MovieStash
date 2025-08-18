package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class GetContentByCelebrityUseCase(
    private val repository: ContentRepository
) {

    operator fun invoke(
        celebrityId: Int,
    ) = repository.getContentByCelebrityFlow(celebrityId)
}