package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.CollectionRepository

class GetPublicCollectionsUseCase(
    private val repository: CollectionRepository
) {

    operator fun invoke() = repository.getEditorCollectionsFlow()
}