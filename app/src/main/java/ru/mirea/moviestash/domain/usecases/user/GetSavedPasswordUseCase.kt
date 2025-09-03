package ru.mirea.moviestash.domain.usecases.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.EncryptionManager
import javax.inject.Inject

class GetSavedPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val encryptionManager: EncryptionManager
) {

    operator fun invoke(): Flow<String> {
        return authRepository.getSavedPassword().map { encryptedPassword ->
            val savedLogin = authRepository.getSavedLogin().first()
            if (savedLogin.isNotBlank()) {
                encryptionManager.decryptData(
                    encryptedPassword,
                    savedLogin
                )
            } else {
                encryptedPassword
            }
        }
    }

}