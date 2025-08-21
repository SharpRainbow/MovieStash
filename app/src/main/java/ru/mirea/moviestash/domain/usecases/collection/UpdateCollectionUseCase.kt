package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CollectionRepository
import javax.inject.Inject

class UpdateCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        collectionId: Int,
        name: String,
        description: String?
    ) {
        collectionRepository.updateCollection(
            authRepository.getValidToken(),
            collectionId,
            name,
            description,
        )
    }
}