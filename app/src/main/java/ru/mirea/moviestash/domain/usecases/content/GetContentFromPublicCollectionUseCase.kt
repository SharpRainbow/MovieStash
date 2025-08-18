package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class GetContentFromPublicCollectionUseCase(
    private val repository: ContentRepository
) {

    operator fun invoke(
        collectionId: Int
    ) = repository.getContentFromPublicCollectionFlow(
        collectionId
    )
}