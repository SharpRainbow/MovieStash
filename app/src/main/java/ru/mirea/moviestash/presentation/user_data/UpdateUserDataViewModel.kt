package ru.mirea.moviestash.presentation.user_data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.savedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.UserRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.UserEntity
import ru.mirea.moviestash.domain.usecases.user.GetUserDataUseCase
import ru.mirea.moviestash.domain.usecases.user.ObserveUserDataUseCase
import ru.mirea.moviestash.domain.usecases.user.UpdateUserDataUseCase

class UpdateUserDataViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<UpdateUserDataScreenState>(
        UpdateUserDataScreenState.Loading
    )
    val state = _state.asStateFlow()

    private val userRepository = UserRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi,
    )
    private val observeUserDataUseCase = ObserveUserDataUseCase(
        userRepository
    )
    private val getUserDataUseCase = GetUserDataUseCase(
        userRepository,
        authRepository
    )
    private val updateUserDataUseCase = UpdateUserDataUseCase(
        userRepository,
        authRepository
    )

    init {
        getUserData()
        observeUserDataUseCase().onEach { userDataResult ->
            when(userDataResult) {
                Result.Empty -> {}
                is Result.Error -> {
                    _state.update {
                        UpdateUserDataScreenState.Error(
                            dataError = true
                        )
                    }
                }
                is Result.Success<UserEntity> -> {
                    _state.update {
                        UpdateUserDataScreenState.Editing(
                            userData = userDataResult.data
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateUserData(
        nickname: String?,
        email: String?,
        password: String?
    ) {
        if (nickname.isNullOrBlank() && email.isNullOrBlank() && password.isNullOrBlank()) {
            _state.update {
                UpdateUserDataScreenState.Error(
                    errorInputNickname = nickname.isNullOrBlank(),
                    errorInputEmail = email.isNullOrBlank()
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { UpdateUserDataScreenState.Loading }
            try {
                updateUserDataUseCase(
                    nickname = nickname,
                    email = email,
                    password = password
                )
                _state.update { UpdateUserDataScreenState.Saved }
            } catch (e: Exception) {
                _state.update {
                    UpdateUserDataScreenState.Error(
                        dataError = true,
                    )
                }
            }
        }
    }

    fun resetErrorInputNickname() {
        _state.update { previousState ->
            if (previousState is UpdateUserDataScreenState.Error) {
                previousState.copy(errorInputNickname = false)
            } else {
                previousState
            }
        }
    }

    fun resetErrorInputEmail() {
        _state.update { previousState ->
            if (previousState is UpdateUserDataScreenState.Error) {
                previousState.copy(errorInputEmail = false)
            } else {
                previousState
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            getUserDataUseCase()
        }
    }
}

sealed interface UpdateUserDataScreenState {
    data object Loading : UpdateUserDataScreenState
    data class Editing(
        val userData: UserEntity
    ) : UpdateUserDataScreenState
    data object Saved : UpdateUserDataScreenState
    data class Error(
        val dataError: Boolean = false,
        val errorInputNickname: Boolean = false,
        val errorInputEmail: Boolean = false,
    ) : UpdateUserDataScreenState
}