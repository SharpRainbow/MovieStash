package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class ObserveContentByCelebrityUseCase(
    private val repository: ContentRepository
) {

    operator fun invoke() = repository.contentListFlow
}