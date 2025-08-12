package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository

class GetCredentialByLoginUseCase(
    private val repository: CredentialsRepository
) {

    suspend operator fun invoke(login: String) =
        repository.getByLogin(login)
}