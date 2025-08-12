package ru.mirea.moviestash.domain.usecases.stars

import ru.mirea.moviestash.domain.UserStarRepository

class ObserveRatingUseCase(
    private val userStarRepository: UserStarRepository
) {

    operator fun invoke() = userStarRepository.userStarFlow
}