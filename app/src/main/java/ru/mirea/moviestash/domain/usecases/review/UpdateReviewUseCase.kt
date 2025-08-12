package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.ReviewRepository
import ru.mirea.moviestash.domain.UserRepository

class UpdateReviewUseCase(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        reviewId: Int,
        title: String,
        text: String,
        opinionId: Int
    ) = reviewRepository.updateReview(
        authRepository.getValidToken(),
        title,
        text,
        reviewId,
        opinionId
    )
}