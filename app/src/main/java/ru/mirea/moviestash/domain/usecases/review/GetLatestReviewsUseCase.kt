package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.ReviewRepository
import ru.mirea.moviestash.domain.entities.ReviewEntity
import javax.inject.Inject

class GetLatestReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        contentId: Int,
        limit: Int = 5
    ): List<ReviewEntity> {
        val token = jwtManager.getValidToken()
        return if (token.isNotBlank()) {
            reviewRepository.getFirstNReviewsByContentId(
                contentId,
                limit,
                token
            )
        } else {
            reviewRepository.getFirstNReviewsByContentId(
                contentId,
                limit,
                null
            )
        }
    }
}