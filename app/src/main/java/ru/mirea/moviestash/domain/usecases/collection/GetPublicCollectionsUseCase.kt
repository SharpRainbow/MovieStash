package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.CollectionRepository

class GetPublicCollectionsUseCase(
    private val repository: CollectionRepository
) {

    suspend operator fun invoke(
        page: Int,
        limit: Int
    ) = repository.getEditorCollections(page, limit)
}