package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository
import ru.mirea.moviestash.domain.JwtManager
import javax.inject.Inject

class GetContentFromUserCollectionUseCase @Inject constructor(
    private val contentRepository: ContentRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        collectionId: Int
    ) = contentRepository.getContentFromUserCollectionFlow(
        jwtManager.getValidToken(),
        collectionId
    )
}