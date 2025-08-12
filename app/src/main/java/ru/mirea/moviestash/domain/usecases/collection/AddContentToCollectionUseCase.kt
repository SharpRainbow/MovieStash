package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CollectionRepository

class AddContentToCollectionUseCase(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(collectionId: Int, contentId: Int) {
        collectionRepository.addContentToCollection(
            authRepository.getValidToken(),
            collectionId,
            contentId
        )
    }
}