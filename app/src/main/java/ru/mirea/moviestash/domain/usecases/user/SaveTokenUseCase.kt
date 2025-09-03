package ru.mirea.moviestash.domain.usecases.user

import kotlinx.coroutines.flow.first
import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.EncryptionManager
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val encryptionManager: EncryptionManager
) {

    suspend operator fun invoke(token: String) {
        val savedLogin = authRepository.getSavedLogin().first()
        authRepository.saveToken(
            encryptionManager.encryptData(
                token,
                savedLogin
            )
        )
    }
}