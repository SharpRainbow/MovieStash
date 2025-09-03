package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.CollectionRepository
import ru.mirea.moviestash.domain.JwtManager
import javax.inject.Inject

class GetUserCollectionsUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke() = collectionRepository.getUserCollectionsFlow(
        jwtManager.getValidToken()
    )
}