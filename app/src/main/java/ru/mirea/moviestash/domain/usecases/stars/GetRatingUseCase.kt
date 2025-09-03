package ru.mirea.moviestash.domain.usecases.stars

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.UserStarRepository
import javax.inject.Inject

class GetRatingUseCase @Inject constructor(
    private val userStarRepository: UserStarRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        contentId: Int,
    ) = userStarRepository.getUserStarByContentId(
        jwtManager.getValidToken(),
        contentId
    )
}