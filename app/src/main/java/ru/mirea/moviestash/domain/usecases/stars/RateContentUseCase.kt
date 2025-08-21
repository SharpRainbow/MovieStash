package ru.mirea.moviestash.domain.usecases.stars

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserStarRepository
import javax.inject.Inject

class RateContentUseCase @Inject constructor(
    private val userStarRepository: UserStarRepository,
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(
        contentId: Int,
        rating: Int,
    ) {
        userStarRepository.addUserStar(
            authRepository.getValidToken(),
            contentId,
            rating,
        )
    }
}