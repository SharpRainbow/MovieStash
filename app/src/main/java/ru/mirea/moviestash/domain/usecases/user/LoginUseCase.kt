package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        login: String,
        password: String
    ) {
        authRepository.login(login, password)
    }
}