package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.CollectionRepository

class GetPublicCollectionInfoUseCase(
    private val collectionRepository: CollectionRepository
) {

    suspend operator fun invoke(collectionId: Int) = collectionRepository.getPublicCollectionInfo(
        collectionId
    )
}