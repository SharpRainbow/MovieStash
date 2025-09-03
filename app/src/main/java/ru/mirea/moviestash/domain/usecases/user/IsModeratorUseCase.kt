package ru.mirea.moviestash.domain.usecases.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.entities.Role
import javax.inject.Inject

class IsModeratorUseCase @Inject constructor(
    private val jwtManager: JwtManager,
    private val getTokenUseCase: GetTokenUseCase
) {

    operator fun invoke(): Flow<Boolean> {
        return getTokenUseCase().map { savedToken ->
            if (savedToken.isNotBlank()) {
                jwtManager.parseJwtToken(savedToken).role == Role.MODERATOR
            } else {
                false
            }
        }
    }
}