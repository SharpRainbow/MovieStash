package ru.mirea.moviestash.domain.usecases.stars

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserStarRepository
import javax.inject.Inject

class UpdateRatingUseCase @Inject constructor(
    private val userStarRepository: UserStarRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        starId: Int,
        rating: Int
    ) {
        userStarRepository.updateUserStar(
            authRepository.getValidToken(),
            starId,
            rating
        )
    }
}