package ru.mirea.moviestash.presentation.news_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.NewsRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.domain.usecases.news.GetNewsListUseCase
import ru.mirea.moviestash.domain.usecases.news.ObserveNewsListUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase

class NewsListViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(
        NewsListScreenState()
    )
    val state = _state.asStateFlow()

    private val newsRepository = NewsRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val isModeratorUseCase = IsModeratorUseCase(
        authRepository
    )
    private val observeNewsListUseCase = ObserveNewsListUseCase(
        newsRepository
    )
    private val getNewsListUseCase = GetNewsListUseCase(
        newsRepository
    )
    private var page = FIRST_PAGE
    private val limit = 20

    init {
        getNewsList()
        observeNewsListUseCase().onEach { newsListResult ->
            when (newsListResult) {
                Result.Empty -> {}
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            error = newsListResult.exception
                        )
                    }
                }

                is Result.Success<List<NewsEntity>> -> {
                    _state.update { state ->
                        state.copy(
                            error = null,
                            newsList = state.newsList + newsListResult.data
                        )
                    }
                    if (newsListResult.data.isNotEmpty()) {
                        page++
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNewsList() {
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            getNewsListUseCase(
                page,
                limit
            )
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun isModerator() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    isModerator = isModeratorUseCase()
                )
            }
        }
    }

    fun resetPage() {
        page = FIRST_PAGE
        _state.update { state ->
            state.copy(
                newsList = emptyList()
            )
        }
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}

data class NewsListScreenState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isModerator: Boolean = false,
    val newsList: List<NewsEntity> = emptyList()
)