package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.ReviewRepository
import javax.inject.Inject

class UpdateReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        reviewId: Int,
        title: String,
        text: String,
        opinionId: Int
    ) = reviewRepository.updateReview(
        jwtManager.getValidToken(),
        title,
        text,
        reviewId,
        opinionId
    )
}