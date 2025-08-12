package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository

class RemoveCredentialUseCase(
    private val repository: CredentialsRepository
) {

    suspend operator fun invoke(login: String) {
        repository.removeCredential(login)
    }
}