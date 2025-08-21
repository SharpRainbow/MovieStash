package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository
import javax.inject.Inject

class GetContentFromPublicCollectionUseCase @Inject constructor(
    private val repository: ContentRepository
) {

    operator fun invoke(
        collectionId: Int
    ) = repository.getContentFromPublicCollectionFlow(
        collectionId
    )
}