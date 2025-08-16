package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserRepository

class UpdateUserDataUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        nickname: String?,
        email: String?,
        password: String?
    ) {
        userRepository.updateUserData(
            authRepository.getValidToken(),
            nickname,
            email,
            password
        )
    }
}