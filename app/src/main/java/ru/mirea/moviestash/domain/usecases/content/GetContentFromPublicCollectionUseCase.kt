package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class GetContentFromPublicCollectionUseCase(
    private val repository: ContentRepository
) {

    suspend operator fun invoke(
        collectionId: Int,
        page: Int,
        limit: Int
    ) = repository.getContentFromPublicCollection(
        collectionId,
        page,
        limit
    )
}