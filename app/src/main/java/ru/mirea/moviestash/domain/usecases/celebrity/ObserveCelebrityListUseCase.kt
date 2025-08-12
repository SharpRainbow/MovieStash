package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository

class ObserveCelebrityListUseCase(
    private val repository: CelebrityRepository
) {

    operator fun invoke() = repository.celebrityListFlow
}