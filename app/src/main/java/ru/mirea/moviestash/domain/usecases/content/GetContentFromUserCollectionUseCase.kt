package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.ContentRepository
import ru.mirea.moviestash.domain.UserRepository

class GetContentFromUserCollectionUseCase(
    private val contentRepository: ContentRepository,
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(
        collectionId: Int
    ) = contentRepository.getContentFromUserCollectionFlow(
        authRepository.getValidToken(),
        collectionId
    )
}