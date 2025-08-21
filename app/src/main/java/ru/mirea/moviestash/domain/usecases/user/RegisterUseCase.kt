package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        login: String,
        password: String,
        nickname: String,
        email: String
    ) {
        repository.register(
            login = login,
            password = password,
            nickname = nickname,
            email = email
        )
    }
}