package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.ReviewRepository
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(reviewId: Int) = reviewRepository.deleteReview(
        authRepository.getValidToken(),
        reviewId
    )
}