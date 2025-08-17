package ru.mirea.moviestash.presentation.account

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.UserRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.UserEntity
import ru.mirea.moviestash.domain.usecases.user.GetUserDataUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase
import ru.mirea.moviestash.domain.usecases.user.LogoutUseCase
import ru.mirea.moviestash.domain.usecases.user.ObserveUserDataUseCase

class AccountViewModel(
    private val application: Application
): AndroidViewModel(application) {

    private val _state = MutableStateFlow<AccountState>(AccountState.Loading)
    val state = _state.asStateFlow()

    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val userRepository = UserRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeUserDataUseCase = ObserveUserDataUseCase(
        userRepository
    )
    private val getUserDataUseCase = GetUserDataUseCase(
        userRepository,
        authRepository
    )
    private val isModeratorUseCase = IsModeratorUseCase(
        authRepository
    )
    private val logoutUseCase = LogoutUseCase(
        authRepository
    )

    init {
        getUserData()
        observeUserDataUseCase().onEach { userDataResult ->
            when (userDataResult) {
                is Result.Success<UserEntity> -> {
                    val isModerator = isModeratorUseCase()
                    _state.value = AccountState.Success(
                        userData = userDataResult.data,
                        isModerator = isModerator
                    )
                }
                is Result.Error -> {
                    _state.value = AccountState.Error
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getUserData() {
        viewModelScope.launch {
            getUserDataUseCase()
        }
    }

    fun logout() {
        logoutUseCase()
    }

}

sealed interface AccountState {
    data object Loading : AccountState
    data class Success(
        val userData: UserEntity,
        val isModerator: Boolean = false
    ) : AccountState
    data object Error : AccountState
}