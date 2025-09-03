package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.EncryptionManager
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val encryptionManager: EncryptionManager
) {

    suspend operator fun invoke(
        login: String,
        password: String
    ) {
        val token = authRepository.login(login, password)
        authRepository.saveCredentials(
            login,
            encryptionManager.encryptData(password, login),
            encryptionManager.encryptData(token, login)
        )
    }
}