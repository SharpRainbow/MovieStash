package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository

class IsLoggedInUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Boolean {
        return authRepository.isLoggedIn()
    }
}