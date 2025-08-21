package ru.mirea.moviestash.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.CredentialsEntity
import ru.mirea.moviestash.domain.usecases.credentials.GetCredentialByLoginUseCase
import ru.mirea.moviestash.domain.usecases.credentials.GetSavedCredentialsUseCase
import ru.mirea.moviestash.domain.usecases.credentials.RemoveCredentialUseCase
import ru.mirea.moviestash.domain.usecases.credentials.SaveCredentialsUseCase
import ru.mirea.moviestash.domain.usecases.user.LoginUseCase
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val getCredentialsUseCase: GetSavedCredentialsUseCase,
    private val getCredentialByLoginUseCase: GetCredentialByLoginUseCase,
    private val loginUseCase: LoginUseCase,
    private val saveCredentialsUseCase: SaveCredentialsUseCase,
    private val removeCredentialUseCase: RemoveCredentialUseCase,
    private val externalScope: CoroutineScope
): ViewModel() {

    private val _state = MutableStateFlow<LoginState>(
        LoginState.Initial()
    )
    val state = _state.asStateFlow()

    init {
        getCredentialsUseCase().onEach { credentialsEntities ->
            _state.update { state ->
                if (state is LoginState.Initial) {
                    state.copy(
                        credentials = credentialsEntities
                    )
                } else {
                    state
                }
            }
        }.launchIn(viewModelScope)
    }

    fun login(
        login: String?,
        password: String?
    ) {
        viewModelScope.launch {
            if (login.isNullOrBlank() || password.isNullOrBlank()) {
                _state.emit(
                    LoginState.Error(
                        errorInputLogin = login.isNullOrBlank(),
                        errorInputPassword = password.isNullOrBlank()
                    )
                )
                return@launch
            }
            _state.emit(LoginState.Loading)
            try {
                loginUseCase(
                    login,
                    password
                )
                val credentials = getCredentialByLoginUseCase(login)
                _state.emit(
                    LoginState.Success(
                        login = login,
                        password = password,
                        isSaved = credentials != null && credentials.password == password
                    )
                )
            } catch (e: Exception) {
                _state.emit(
                    LoginState.Error(
                        dataError = true
                    )
                )
                return@launch
            }
        }
    }

    fun saveCredentials() {
        externalScope.launch(Dispatchers.IO) {
            val currentState = state.value
            if (currentState is LoginState.Success) {
                saveCredentialsUseCase(
                    currentState.login,
                    currentState.password
                )
            }
        }
    }

    fun removeCredential(login: String) {
        viewModelScope.launch {
            removeCredentialUseCase(login)
        }
    }

    fun resetErrorInputLogin() {
        _state.update { state ->
            if (state is LoginState.Error) {
                state.copy(errorInputLogin = false)
            } else {
                state
            }
        }
    }

    fun resetErrorInputPassword() {
        _state.update { state ->
            if (state is LoginState.Error) {
                state.copy(errorInputPassword = false)
            } else {
                state
            }
        }
    }
}

sealed interface LoginState {
    data class Initial(
        val credentials: List<CredentialsEntity> = emptyList()
    ) : LoginState
    data object Loading : LoginState
    data class Success(
        val login: String,
        val password: String,
        val isSaved: Boolean = false
    ) : LoginState
    data class Error(
        val dataError: Boolean = false,
        val errorInputLogin: Boolean = false,
        val errorInputPassword: Boolean = false,
    ) : LoginState
}