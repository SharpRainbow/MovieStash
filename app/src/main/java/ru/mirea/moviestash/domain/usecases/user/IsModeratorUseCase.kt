package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository

class IsModeratorUseCase(
    private val repository: AuthRepository
) {

    operator fun invoke() = repository.isModerator()
}