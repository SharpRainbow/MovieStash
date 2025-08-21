package ru.mirea.moviestash.presentation.account

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
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase
import ru.mirea.moviestash.domain.usecases.user.LogoutUseCase
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val isModeratorUseCase: IsModeratorUseCase,
    private val logoutUseCase: LogoutUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<AccountState>(AccountState.Loading)
    val state = _state.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getUserDataUseCase().onEach { userData ->
                if (userData.isSuccess) {
                    _state.update {
                        AccountState.Success(
                            userData = userData.getOrThrow(),
                            isModerator = isModeratorUseCase()
                        )
                    }
                } else {
                    _state.update {
                        AccountState.Error
                    }
                }
            }.collect()
        }
    }

    fun logout() {
        logoutUseCase()
        viewModelScope.launch {
            _state.emit(AccountState.LoggedOut)
        }
    }

}

sealed interface AccountState {
    data object Loading : AccountState
    data class Success(
        val userData: UserEntity,
        val isModerator: Boolean = false
    ) : AccountState
    data object LoggedOut : AccountState
    data object Error : AccountState
}