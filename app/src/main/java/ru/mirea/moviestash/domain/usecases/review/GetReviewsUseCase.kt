package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.ReviewRepository
import javax.inject.Inject

class GetReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {

    operator fun invoke(contentId: Int) = reviewRepository.getReviewsByContentId(contentId)
}