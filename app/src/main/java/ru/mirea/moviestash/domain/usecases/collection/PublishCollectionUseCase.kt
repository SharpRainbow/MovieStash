package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CollectionRepository
import ru.mirea.moviestash.domain.UserRepository

class PublishCollectionUseCase(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(collectionId: Int) {
        collectionRepository.publishCollection(
            authRepository.getValidToken(),
            collectionId
        )
    }
}