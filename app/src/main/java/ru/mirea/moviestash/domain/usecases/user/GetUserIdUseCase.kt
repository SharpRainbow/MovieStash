package ru.mirea.moviestash.domain.usecases.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mirea.moviestash.domain.JwtManager
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
    private val jwtManager: JwtManager
) {

    operator fun invoke(): Flow<Int> {
        return getTokenUseCase().map { savedToken ->
            if (savedToken.isNotBlank()) {
                jwtManager.parseJwtToken(savedToken).userId
            } else {
                0
            }
        }
    }
}