package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.ReviewRepository
import javax.inject.Inject

class GetReviewByIdUseCase @Inject constructor(
    private val repository: ReviewRepository
) {

    operator fun invoke(reviewId: Int) = repository.getReviewById(reviewId)
}