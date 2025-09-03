package ru.mirea.moviestash.domain.usecases.stars

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.UserStarRepository
import javax.inject.Inject

class UpdateRatingUseCase @Inject constructor(
    private val userStarRepository: UserStarRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        starId: Int,
        rating: Int
    ) {
        userStarRepository.updateUserStar(
            jwtManager.getValidToken(),
            starId,
            rating
        )
    }
}