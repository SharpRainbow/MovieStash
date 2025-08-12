package ru.mirea.moviestash.presentation.banned_users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.UserRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.BannedUserEntity
import ru.mirea.moviestash.domain.usecases.user.GetBannedUsersUseCase
import ru.mirea.moviestash.domain.usecases.user.ObserveBannedUsersUseCase
import ru.mirea.moviestash.domain.usecases.user.UnbanUserUseCase
import ru.mirea.moviestash.Result

class BannedUsersViewModel(
    private val application: Application
): AndroidViewModel(application) {

    private val _state = MutableStateFlow(
        BannedUsersScreenState()
    )
    val state = _state.asStateFlow()

    private val userRepository = UserRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi,
    )
    private val observeBannedUsersUseCase = ObserveBannedUsersUseCase(
        userRepository,
    )
    private val getBannedUsersUseCase = GetBannedUsersUseCase(
        userRepository,
        authRepository
    )
    private val unbanUserUseCase = UnbanUserUseCase(
        userRepository,
        authRepository
    )
    private var page = FIRST_PAGE

    init {
        getBannedUsers()
        observeBannedUsersUseCase().onEach { userListResult ->
            when (userListResult) {
                is Result.Success -> {
                    _state.update { state ->
                        BannedUsersScreenState(
                            bannedUsers = state.bannedUsers + userListResult.data,
                        )
                    }
                    if (userListResult.data.isNotEmpty()) {
                        page++
                    }
                }

                is Result.Error -> {
                    BannedUsersScreenState(
                        error = userListResult.exception
                    )
                }

                is Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getBannedUsers() {
        _state.update { state ->
            state.copy(
                isLoading = true,
            )
        }
        viewModelScope.launch {
            getBannedUsersUseCase(
                page,
                LIMIT
            )
            _state.update { state ->
                state.copy(
                    isLoading = false,
                    error = null,
                )
            }
        }
    }

    fun unbanUser(userId: Int) {
        viewModelScope.launch {
            try {
                unbanUserUseCase(userId)
                _state.update { state ->
                    BannedUsersScreenState(
                        bannedUsers = state.bannedUsers.filter { it.id != userId },
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

    companion object {

        private const val FIRST_PAGE = 1
        private const val LIMIT = 20
    }
}

data class BannedUsersScreenState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val bannedUsers: List<BannedUserEntity> = emptyList()
)