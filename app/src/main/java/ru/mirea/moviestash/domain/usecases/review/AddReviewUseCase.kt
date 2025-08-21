package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.ReviewRepository
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        contentId: Int,
        title: String,
        reviewText: String,
        opinionId: Int
    ) {
        reviewRepository.addReview(
            authRepository.getValidToken(),
            contentId,
            title,
            reviewText,
            opinionId
        )
    }
}

