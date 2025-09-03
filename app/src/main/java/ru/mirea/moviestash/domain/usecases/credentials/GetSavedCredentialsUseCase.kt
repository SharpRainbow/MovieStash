package ru.mirea.moviestash.domain.usecases.credentials

import kotlinx.coroutines.flow.map
import ru.mirea.moviestash.domain.CredentialsRepository
import ru.mirea.moviestash.domain.EncryptionManager
import javax.inject.Inject

class GetSavedCredentialsUseCase @Inject constructor(
    private val repository: CredentialsRepository,
    private val encryptionManager: EncryptionManager
) {

    operator fun invoke() = repository.getCredentials().map {
        it.map { cred ->
            cred.copy(
                password = encryptionManager.decryptData(
                    cred.password,
                    cred.login
                )
            )
        }
    }
}