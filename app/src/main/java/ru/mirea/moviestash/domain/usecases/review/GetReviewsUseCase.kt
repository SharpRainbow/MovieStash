package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.ReviewRepository

class GetReviewsUseCase(
    private val reviewRepository: ReviewRepository
) {

    suspend operator fun invoke(contentId: Int, page: Int, limit: Int, preview: Boolean) {
        reviewRepository.getReviewsByContentId(contentId, page, limit, preview)
    }
}