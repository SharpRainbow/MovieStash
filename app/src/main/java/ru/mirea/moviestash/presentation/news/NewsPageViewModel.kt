package ru.mirea.moviestash.presentation.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.mirea.moviestash.Utils.mergeWith
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.domain.usecases.news.DeleteNewsUseCase
import ru.mirea.moviestash.domain.usecases.news.GetNewsByIdUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase
import javax.inject.Inject

class NewsPageViewModel @Inject constructor(
    private val newsId: Int,
    private val getNewsByIdUseCase: GetNewsByIdUseCase,
    private val deleteNewsUseCase: DeleteNewsUseCase,
    private val isModeratorUseCase: IsModeratorUseCase,
) : ViewModel() {

    private val deletedFlow = MutableSharedFlow<NewsPageState>()
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: Flow<NewsPageState> =
        getNewsByIdUseCase(newsId)
            .map { newsResult ->
                if (newsResult.isSuccess) {
                    NewsPageState.Success(
                        news = newsResult.getOrThrow(),
                        isModerator = isModeratorUseCase()
                    )
                } else {
                    NewsPageState.Error
                }
            }
            .onStart {
                NewsPageState.Loading
            }
            .mergeWith(deletedFlow)

    fun deleteNews() {
        viewModelScope.launch {
            try {
                deleteNewsUseCase(newsId)
                deletedFlow.emit(
                    NewsPageState.Deleted
                )
            } catch (e: Exception) {
                deletedFlow.emit(
                    NewsPageState.Error
                )
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