package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository
import javax.inject.Inject

class GetContentByCelebrityUseCase @Inject constructor(
    private val repository: ContentRepository
) {

    operator fun invoke(
        celebrityId: Int,
    ) = repository.getContentByCelebrityFlow(celebrityId)
}