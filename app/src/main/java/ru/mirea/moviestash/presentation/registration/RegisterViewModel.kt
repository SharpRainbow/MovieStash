package ru.mirea.moviestash.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.usecases.user.RegisterUseCase
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
): ViewModel() {

    private val _state = MutableStateFlow<RegisterScreenState>(
        RegisterScreenState.Initial
    )
    val state = _state.asStateFlow()

    fun register(
        login: String?,
        password: String?,
        nickname: String?,
        email: String?
    ) {
        if (login.isNullOrBlank() || password.isNullOrBlank()
            || nickname.isNullOrBlank() || email.isNullOrBlank()) {
            _state.update {
                RegisterScreenState.Error(
                    errorInputLogin = login.isNullOrBlank(),
                    errorInputPassword = password.isNullOrBlank(),
                    errorInputNickname = nickname.isNullOrBlank(),
                    errorInputEmail = email.isNullOrBlank()
                )
            }
            return
        }
        viewModelScope.launch {
            try {
                registerUseCase(
                    login = login,
                    password = password,
                    nickname = nickname,
                    email = email
                )
                _state.update {
                    RegisterScreenState.Success
                }
            } catch (e: Exception) {
                _state.update {
                    RegisterScreenState.Error(
                        network = true
                    )
                }
            }
        }
    }

    fun resetErrorInputLogin() {
        _state.update {
            if (it is RegisterScreenState.Error) {
                it.copy(errorInputLogin = false)
            } else {
                it
            }
        }
    }

    fun resetErrorInputPassword() {
        _state.update {
            if (it is RegisterScreenState.Error) {
                it.copy(errorInputPassword = false)
            } else {
                it
            }
        }
    }

    fun resetErrorInputEmail() {
        _state.update {
            if (it is RegisterScreenState.Error) {
                it.copy(errorInputEmail = false)
            } else {
                it
            }
        }
    }
    fun resetErrorInputNickname() {
        _state.update {
            if (it is RegisterScreenState.Error) {
                it.copy(errorInputNickname = false)
            } else {
                it
            }
        }
    }

}

sealed class RegisterScreenState {
    data object Initial: RegisterScreenState()
    data object Loading: RegisterScreenState()
    data class Error(
        val errorInputLogin: Boolean = false,
        val errorInputPassword: Boolean = false,
        val errorInputNickname: Boolean = false,
        val errorInputEmail: Boolean = false,
        val network: Boolean = false
    ): RegisterScreenState()
    data object Success: RegisterScreenState()
}