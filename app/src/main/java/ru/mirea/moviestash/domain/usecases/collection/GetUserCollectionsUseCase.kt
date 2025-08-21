package ru.mirea.moviestash.domain.usecases.collection

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.CollectionRepository
import javax.inject.Inject

class GetUserCollectionsUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() = collectionRepository.getUserCollectionsFlow(
        authRepository.getValidToken()
    )
}