package ru.mirea.moviestash.domain.usecases.stars

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserStarRepository
import javax.inject.Inject

class GetRatingUseCase @Inject constructor(
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