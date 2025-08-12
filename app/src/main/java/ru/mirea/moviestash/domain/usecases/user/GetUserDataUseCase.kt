package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserRepository

class GetUserDataUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() = userRepository.getUserData(
        authRepository.getValidToken()
    )
}