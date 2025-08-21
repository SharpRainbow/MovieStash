package ru.mirea.moviestash.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.domain.entities.GenreEntity
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.domain.usecases.content.GetMainPageContentUseCase
import ru.mirea.moviestash.domain.usecases.genre.GetPresentGenresUseCase
import ru.mirea.moviestash.domain.usecases.news.GetLatestNewsUseCase
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getLatestNewsUseCase: GetLatestNewsUseCase,
    private val getMainPageContentUseCase: GetMainPageContentUseCase,
    private val getPresentGenresUseCase: GetPresentGenresUseCase
): ViewModel() {

    private val _state = MutableStateFlow(
        HomeScreenState()
    )
    val state = _state.asStateFlow()

    init {
        reloadPage()
    }

    fun getPresentGenres() {
        viewModelScope.launch {
            try {
                val genreResult = getPresentGenresUseCase()
                _state.update { state ->
                    state.copy(
                        error = null,
                        collections = genreResult
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

    private fun getMainPageContent() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    isLoading = true,
                    error = null
                )
            }
            try {
                _state.update { state ->
                    state.copy(
                        contents = getMainPageContentUseCase()
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
            _state.update { state ->
                state.copy(
                    isLoading = false
                )
            }
        }
    }

    private fun getLatestNews() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    error = null,
                    isLoading = true
                )
            }
            try {
                val newsResult = getLatestNewsUseCase(NEWS_LIMIT)
                _state.update { state ->
                    state.copy(
                        news = newsResult
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
            _state.update { state ->
                state.copy(
                    isLoading = false
                )
            }
        }
    }

    fun reloadPage() {
        getMainPageContent()
        getPresentGenres()
        getLatestNews()
    }

    companion object {
        private const val NEWS_LIMIT = 5
    }
}

data class HomeScreenState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val contents: List<ContentEntityBase> = emptyList(),
    val collections: List<GenreEntity> = emptyList(),
    val news: List<NewsEntity> = emptyList()
)