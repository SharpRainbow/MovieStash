package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository
import ru.mirea.moviestash.domain.EncryptionManager
import javax.inject.Inject

class SaveCredentialsUseCase @Inject constructor(
    private val repository: CredentialsRepository,
    private val encryptionManager: EncryptionManager
) {

    suspend operator fun invoke(
        login: String,
        password: String,
    ) = repository.addCredentials(
        login,
        encryptionManager.encryptData(password, login)
    )
}