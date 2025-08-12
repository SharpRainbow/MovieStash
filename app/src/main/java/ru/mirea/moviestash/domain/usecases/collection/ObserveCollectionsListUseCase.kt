package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.CollectionRepository

class ObserveCollectionsListUseCase(
    private val repository: CollectionRepository
) {

    operator fun invoke() = repository.collectionsListFlow
}