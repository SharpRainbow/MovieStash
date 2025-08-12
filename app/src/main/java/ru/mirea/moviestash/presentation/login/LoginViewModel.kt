package ru.mirea.moviestash.presentation.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.MovieStashApplication
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.CredentialsRepositoryImpl
import ru.mirea.moviestash.data.UserRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.CredentialsEntity
import ru.mirea.moviestash.domain.usecases.credentials.GetCredentialByLoginUseCase
import ru.mirea.moviestash.domain.usecases.credentials.GetSavedCredentialsUseCase
import ru.mirea.moviestash.domain.usecases.credentials.RemoveCredentialUseCase
import ru.mirea.moviestash.domain.usecases.credentials.SaveCredentialsUseCase
import ru.mirea.moviestash.domain.usecases.user.LoginUseCase

class LoginViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<LoginState>(
        LoginState.Initial()
    )
    val state = _state.asStateFlow()

    private val userRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val credentialsRepository = CredentialsRepositoryImpl(
        application
    )
    private val loginUseCase = LoginUseCase(
        userRepository
    )
    private val getCredentialsUseCase = GetSavedCredentialsUseCase(
        credentialsRepository
    )
    private val saveCredentialsUseCase = SaveCredentialsUseCase(
        credentialsRepository
    )
    private val getCredentialByLoginUseCase = GetCredentialByLoginUseCase(
        credentialsRepository
    )
    private val removeCredentialUseCase = RemoveCredentialUseCase(
        credentialsRepository
    )

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
        username: String?,
        password: String?
    ) {
        viewModelScope.launch {
            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                _state.emit(
                    LoginState.Error(
                        "Username and password cannot be empty"
                    )
                )
                return@launch
            }
            _state.emit(LoginState.Loading)
            try {
                loginUseCase(
                    username,
                    password
                )
                val credentials = getCredentialByLoginUseCase(username)
                _state.emit(
                    LoginState.Success(
                        login = username,
                        password = password,
                        isSaved = credentials != null && credentials.password == password
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.emit(
                    LoginState.Error(
                        "Login failed: ${e.message}"
                    )
                )
                return@launch
            }
        }
    }

    fun saveCredentials() {
        (application as MovieStashApplication).applicationScope.launch(Dispatchers.IO) {
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
    data class Error(val message: String) : LoginState
}