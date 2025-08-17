package ru.mirea.moviestash.domain.usecases.user

import ru.mirea.moviestash.domain.UserRepository

class ObserveUserDataUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke() = userRepository.userDataFlow
}