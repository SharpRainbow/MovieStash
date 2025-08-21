package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import javax.inject.Inject

class IsModeratorUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    operator fun invoke() = repository.isModerator()
}