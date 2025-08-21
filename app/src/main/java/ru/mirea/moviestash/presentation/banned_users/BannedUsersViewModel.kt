package ru.mirea.moviestash.presentation.banned_users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.usecases.user.GetBannedUsersUseCase
import ru.mirea.moviestash.domain.usecases.user.UnbanUserUseCase
import javax.inject.Inject

class BannedUsersViewModel @Inject constructor(
    private val getBannedUsersUseCase: GetBannedUsersUseCase,
    private val unbanUserUseCase: UnbanUserUseCase
): ViewModel() {

    private val _state = MutableStateFlow(
        BannedUsersScreenState()
    )
    val state = _state.asStateFlow()

    private val refreshBannedUsersFlow = MutableSharedFlow<Unit>()
    val bannedUsers = refreshBannedUsersFlow
        .onStart {
            emit(Unit)
        }
        .flatMapLatest {
            getBannedUsersUseCase()
        }
        .cachedIn(viewModelScope)

    fun unbanUser(userId: Int) {
        viewModelScope.launch {
            try {
                unbanUserUseCase(userId)
                refreshBannedUsersFlow.emit(Unit)
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

}

data class BannedUsersScreenState(
    val error: Throwable? = null
)