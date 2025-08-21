package ru.mirea.moviestash.domain.usecases.credentials

import ru.mirea.moviestash.domain.CredentialsRepository
import javax.inject.Inject

class GetCredentialByLoginUseCase @Inject constructor(
    private val repository: CredentialsRepository
) {

    suspend operator fun invoke(login: String) =
        repository.getByLogin(login)
}