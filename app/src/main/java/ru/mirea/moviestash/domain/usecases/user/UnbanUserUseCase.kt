package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserRepository

class UnbanUserUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        userId: Int,
    ) =  userRepository.unban(
        authRepository.getValidToken(),
        userId
    )
}