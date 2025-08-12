package ru.mirea.moviestash.domain.usecases.stars

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserStarRepository

class GetRatingUseCase(
    private val userStarRepository: UserStarRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        contentId: Int,
    ) = userStarRepository.getUserStarByContentId(
        authRepository.getValidToken(),
        contentId
    )
}