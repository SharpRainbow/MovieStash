package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CollectionRepository
import javax.inject.Inject

class DeleteContentFromCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository
){

    suspend operator fun invoke(collectionId: Int, contentId: Int) {
        collectionRepository.deleteContentFromCollection(
            authRepository.getValidToken(),
            collectionId,
            contentId
        )
    }
}