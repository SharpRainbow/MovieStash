package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.CollectionRepository
import ru.mirea.moviestash.domain.JwtManager
import javax.inject.Inject

class DeleteContentFromCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val jwtManager: JwtManager
){

    suspend operator fun invoke(collectionId: Int, contentId: Int) {
        collectionRepository.deleteContentFromCollection(
            jwtManager.getValidToken(),
            collectionId,
            contentId
        )
    }
}