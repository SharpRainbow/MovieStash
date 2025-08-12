package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository

class GetSavedCredentialsUseCase(
    private val repository: CredentialsRepository
) {

    operator fun invoke() = repository.getCredentials()
}