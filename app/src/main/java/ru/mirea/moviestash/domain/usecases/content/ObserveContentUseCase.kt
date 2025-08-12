package ru.mirea.moviestash.domain.usecases.content

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.domain.ContentRepository
import ru.mirea.moviestash.domain.entities.ContentEntity

class ObserveContentUseCase(
    private val repository: ContentRepository
) {

    operator fun invoke(): Flow<Result<ContentEntity>> {
        return repository.contentFlow
    }
}