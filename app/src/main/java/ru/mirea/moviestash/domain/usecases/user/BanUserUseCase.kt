package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.UserRepository
import javax.inject.Inject

class BanUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        userId: Int,
        banReason: String
    ) =  userRepository.ban(
        authRepository.getValidToken(),
        userId,
        banReason
    )
}