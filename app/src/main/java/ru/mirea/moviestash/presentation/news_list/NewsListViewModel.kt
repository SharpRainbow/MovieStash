package ru.mirea.moviestash.presentation.news_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.usecases.news.GetNewsListUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase
import javax.inject.Inject

class NewsListViewModel @Inject constructor(
    private val getNewsListUseCase: GetNewsListUseCase,
    private val isModeratorUseCase: IsModeratorUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(
        NewsListScreenState()
    )
    val state = _state.asStateFlow()

    val newsList = getNewsListUseCase().cachedIn(viewModelScope)

    fun isModerator() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    isModerator = isModeratorUseCase()
                )
            }
        }
    }

}

data class NewsListScreenState(
    val isModerator: Boolean = false
)