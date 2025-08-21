package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.CollectionRepository
import javax.inject.Inject

class GetPublicCollectionInfoUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository
) {

    suspend operator fun invoke(collectionId: Int) = collectionRepository.getPublicCollectionInfo(
        collectionId
    )
}