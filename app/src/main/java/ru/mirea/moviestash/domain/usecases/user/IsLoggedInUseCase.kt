package ru.mirea.moviestash.domain.usecases.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mirea.moviestash.domain.AuthRepository
import javax.inject.Inject

class IsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return authRepository.getToken().map { it.isNotBlank() }
    }
}