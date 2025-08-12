package ru.mirea.moviestash.domain.usecases.review

import ru.mirea.moviestash.domain.ReviewRepository

class ObserveReviewUseCase(
    private val repository: ReviewRepository
) {

    operator fun invoke() = repository.review
}