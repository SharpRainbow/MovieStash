package ru.mirea.moviestash.presentation.home

import android.app.Application
import android.util.Log
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
import ru.mirea.moviestash.data.ContentRepositoryImpl
import ru.mirea.moviestash.data.GenreRepositoryImpl
import ru.mirea.moviestash.data.NewsRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.domain.entities.GenreEntity
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.domain.usecases.news.GetLatestNewsUseCase
import ru.mirea.moviestash.domain.usecases.content.GetMainPageContentUseCase
import ru.mirea.moviestash.domain.usecases.genre.GetPresentGenresUseCase
import ru.mirea.moviestash.domain.usecases.user.IsLoggedInUseCase
import ru.mirea.moviestash.domain.usecases.content.ObserveMainPageContentUseCase
import ru.mirea.moviestash.domain.usecases.news.ObserveNewsListUseCase

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(
        HomeScreenState()
    )
    val state = _state.asStateFlow()

    private val contentRepository = ContentRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val genreRepository = GenreRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val newsRepository = NewsRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi,
    )
    private val observeMainPageContentUseCase = ObserveMainPageContentUseCase(
        contentRepository
    )
    private val getMainPageContentUseCase = GetMainPageContentUseCase(
        contentRepository
    )
    private val getPresentGenresUseCase = GetPresentGenresUseCase(
        genreRepository
    )
    private val observeLatestNewsUseCase = ObserveNewsListUseCase(
        newsRepository
    )
    private val getLatestNewsUseCase = GetLatestNewsUseCase(
        newsRepository
    )
    private val isLoggedInUseCase = IsLoggedInUseCase(
        authRepository
    )

    init {
        reloadPage()
        observeMainPageContentUseCase()
            .onEach { result ->
                when (result) {

                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            error = result.exception
                        )
                    }

                    Result.Empty -> {

                    }

                    is Result.Success<List<ContentEntityBase>> -> {
                        _state.update { state ->
                            state.copy(
                                error = null,
                                contents = result.data
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
        observeLatestNewsUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            error = result.exception
                        )
                    }

                    is Result.Success<List<NewsEntity>> -> {
                        _state.update { state ->
                            state.copy(
                                error = null,
                                news = result.data
                            )
                        }
                    }

                    Result.Empty -> {

                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun getPresentGenres() {
        viewModelScope.launch {
            when(val genreResult = getPresentGenresUseCase()) {
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        error = genreResult.exception
                    )
                }
                is Result.Success<List<GenreEntity>> -> {
                    _state.update { state ->
                        state.copy(
                            error = null,
                            collections = genreResult.data
                        )
                    }
                }
                Result.Empty -> {

                }
            }
        }
    }

    fun getMainPageContent() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    isLoading = true
                )
            }
            getMainPageContentUseCase()
            _state.update { state ->
                state.copy(
                    isLoading = false
                )
            }
        }
    }

    fun getLatestNews() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    isLoading = true
                )
            }
            getLatestNewsUseCase(NEWS_LIMIT)
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