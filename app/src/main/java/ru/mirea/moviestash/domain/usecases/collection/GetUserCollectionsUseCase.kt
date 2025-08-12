package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CollectionRepository

class GetUserCollectionsUseCase(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(page: Int, limit: Int) {
        collectionRepository.getUserCollections(
            authRepository.getValidToken(),
            page,
            limit
        )
    }
}