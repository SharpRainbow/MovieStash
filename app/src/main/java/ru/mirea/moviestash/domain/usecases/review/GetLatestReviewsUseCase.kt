package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.ReviewRepository
import ru.mirea.moviestash.domain.entities.ReviewEntity
import javax.inject.Inject

class GetLatestReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        contentId: Int,
        limit: Int = 5
    ): List<ReviewEntity> {
        val token = authRepository.getValidToken()
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