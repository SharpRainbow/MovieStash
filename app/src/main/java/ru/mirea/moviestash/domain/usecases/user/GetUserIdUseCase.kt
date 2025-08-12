package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository

class GetUserIdUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke() = authRepository.getUserId()
}