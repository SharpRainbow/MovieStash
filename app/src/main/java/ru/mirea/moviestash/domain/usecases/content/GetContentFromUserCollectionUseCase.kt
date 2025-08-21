package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.ContentRepository
import javax.inject.Inject

class GetContentFromUserCollectionUseCase @Inject constructor(
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