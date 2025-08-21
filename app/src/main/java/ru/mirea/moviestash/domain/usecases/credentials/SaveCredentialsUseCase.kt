package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository
import javax.inject.Inject

class SaveCredentialsUseCase @Inject constructor(
    private val repository: CredentialsRepository
) {

    suspend operator fun invoke(
        login: String,
        password: String,
    ) = repository.addCredentials(
        login, password
    )
}