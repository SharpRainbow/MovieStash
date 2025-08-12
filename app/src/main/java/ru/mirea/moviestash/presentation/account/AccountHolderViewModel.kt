package ru.mirea.moviestash.presentation.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.UserRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.usecases.user.IsLoggedInUseCase

class AccountHolderViewModel(
    private val application: Application
): AndroidViewModel(application) {

    private val repository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val isLoggedInUseCase = IsLoggedInUseCase(
        repository
    )

    fun isLoggedIn(): Boolean {
        return isLoggedInUseCase()
    }
}