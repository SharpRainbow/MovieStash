package ru.mirea.moviestash.presentation.account

import androidx.lifecycle.ViewModel
import ru.mirea.moviestash.domain.usecases.user.IsLoggedInUseCase
import javax.inject.Inject

class AccountHolderViewModel @Inject constructor(
    private val isLoggedInUseCase: IsLoggedInUseCase
): ViewModel() {

    fun isLoggedIn(): Boolean {
        return isLoggedInUseCase()
    }
}