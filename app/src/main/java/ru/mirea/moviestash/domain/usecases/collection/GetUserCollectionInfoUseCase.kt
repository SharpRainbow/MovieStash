package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CollectionRepository

class GetUserCollectionInfoUseCase(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(collectionId: Int) = collectionRepository.getUserCollectionInfo(
        authRepository.getValidToken(),
        collectionId
    )
}