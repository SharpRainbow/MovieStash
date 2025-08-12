package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository

class SaveCredentialsUseCase(
    private val repository: CredentialsRepository
) {

    suspend operator fun invoke(
        login: String,
        password: String,
    ) = repository.addCredentials(
        login, password
    )
}