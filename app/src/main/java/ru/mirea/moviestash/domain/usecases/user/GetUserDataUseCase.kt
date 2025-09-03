package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.UserRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke() = userRepository.getUserData(
        jwtManager.getValidToken()
    )
}