package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository
import javax.inject.Inject

class GetSavedCredentialsUseCase @Inject constructor(
    private val repository: CredentialsRepository
) {

    operator fun invoke() = repository.getCredentials()
}