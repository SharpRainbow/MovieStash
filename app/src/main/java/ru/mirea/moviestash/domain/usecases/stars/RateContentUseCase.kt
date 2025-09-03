package ru.mirea.moviestash.domain.usecases.stars

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.UserStarRepository
import javax.inject.Inject

class RateContentUseCase @Inject constructor(
    private val userStarRepository: UserStarRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        contentId: Int,
        rating: Int,
    ) {
        userStarRepository.addUserStar(
            jwtManager.getValidToken(),
            contentId,
            rating,
        )
    }
}