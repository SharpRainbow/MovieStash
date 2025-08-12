package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.ReviewRepository

class GetReviewByIdUseCase(
    private val repository: ReviewRepository
) {

    suspend operator fun invoke(reviewId: Int) = repository.getReviewById(reviewId)
}