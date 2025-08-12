package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CollectionRepository

class AddCollectionUseCase(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        name: String,
        description: String? = null,
    ) = collectionRepository.addCollection(
        authRepository.getValidToken(),
        name,
        description
    )
}