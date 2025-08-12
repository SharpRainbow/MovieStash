package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserRepository

class GetBannedUsersUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        page: Int,
        limit: Int
    ) = userRepository.getBannedUsers(
        authRepository.getValidToken(),
        page,
        limit
    )
}