package ru.mirea.moviestash.presentation.news

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.data.NewsRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.domain.usecases.news.GetNewsByIdUseCase
import ru.mirea.moviestash.domain.usecases.news.ObserveNewsUseCase
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.domain.usecases.news.DeleteNewsUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase

class NewsPageViewModel(
    private val application: Application,
    private val newsId: Int
): AndroidViewModel(application) {

    private val _state = MutableStateFlow<NewsPageState>(NewsPageState.Loading)
    val state = _state.asStateFlow()

    private val newsRepository = NewsRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeNewsUseCase = ObserveNewsUseCase(
        newsRepository
    )
    private val getNewsByIdUseCase = GetNewsByIdUseCase(
        newsRepository
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val isModeratorUseCase = IsModeratorUseCase(
        authRepository
    )
    private val deleteNewsUseCase = DeleteNewsUseCase(
        newsRepository,
        authRepository
    )

    init {
        getNews()
        observeNewsUseCase().onEach { newsResult ->
            when (newsResult) {
                is Result.Success -> {
                    val news = newsResult.data
                    val isModerator = isModeratorUseCase()
                    _state.value = NewsPageState.Success(
                        news,
                        isModerator
                    )
                }
                is Result.Error -> {
                    _state.value = NewsPageState.Error
                }
                Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getNews() {
        viewModelScope.launch {
            getNewsByIdUseCase(newsId)
        }
    }

    fun deleteNews() {
        viewModelScope.launch {
            try {
                deleteNewsUseCase(newsId)
                _state.update { NewsPageState.Deleted }
            } catch (e: Exception) {
                _state.update { NewsPageState.Error }
            }
        }
    }

    companion object {

        fun provideFactory(
            application: Application,
            newsId: Int
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return NewsPageViewModel(application, newsId) as T
            }
        }
    }
}

sealed interface NewsPageState {
    data object Loading : NewsPageState
    data class Success(
        val news: NewsEntity,
        val isModerator: Boolean = false
    ) : NewsPageState
    data object Deleted : NewsPageState
    data object Error : NewsPageState
}