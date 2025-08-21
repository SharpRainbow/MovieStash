package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository
import javax.inject.Inject

class RemoveCredentialUseCase @Inject constructor(
    private val repository: CredentialsRepository
) {

    suspend operator fun invoke(login: String) {
        repository.removeCredential(login)
    }
}