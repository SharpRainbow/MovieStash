package ru.mirea.moviestash.presentation.user_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.UserEntity
import ru.mirea.moviestash.domain.usecases.user.GetUserDataUseCase
import ru.mirea.moviestash.domain.usecases.user.UpdateUserDataUseCase
import javax.inject.Inject

class UpdateUserDataViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
): ViewModel() {

    private val _state = MutableStateFlow<UpdateUserDataScreenState>(
        UpdateUserDataScreenState.Loading
    )
    val state = _state.asStateFlow()

    init {
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            getUserDataUseCase().onEach { userDataResult ->
                if (userDataResult.isSuccess) {
                    _state.update {
                        UpdateUserDataScreenState.Editing(
                            userData = userDataResult.getOrThrow()
                        )
                    }
                } else {
                    _state.update {
                        UpdateUserDataScreenState.Error(
                            dataError = true
                        )
                    }
                }
            }.collect()
        }
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