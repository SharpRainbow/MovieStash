package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.UserRepository
import javax.inject.Inject

class UpdateUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(
        nickname: String?,
        email: String?,
        password: String?
    ) {
        userRepository.updateUserData(
            jwtManager.getValidToken(),
            nickname,
            email,
            password
        )
    }
}