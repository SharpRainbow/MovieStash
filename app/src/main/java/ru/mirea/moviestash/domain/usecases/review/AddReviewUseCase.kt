package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.ReviewRepository
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        contentId: Int,
        title: String,
        reviewText: String,
        opinionId: Int
    ) {
        reviewRepository.addReview(
            jwtManager.getValidToken(),
            contentId,
            title,
            reviewText,
            opinionId
        )
    }
}

