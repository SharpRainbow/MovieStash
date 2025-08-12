package ru.mirea.moviestash.presentation.news_editor

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mirea.moviestash.data.NewsRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.domain.usecases.news.GetNewsByIdUseCase
import ru.mirea.moviestash.domain.usecases.news.ObserveNewsUseCase
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.domain.usecases.news.AddNewsUseCase
import ru.mirea.moviestash.domain.usecases.news.UpdateNewsUseCase
import java.io.IOException

class NewsEditorViewModel(
    private val application: Application
): AndroidViewModel(application) {

    private val _state = MutableStateFlow<NewsEditorState>(
        NewsEditorState.Initial
    )
    val state = _state.asStateFlow()

    private val newsRepository = NewsRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val observeNewsUseCase = ObserveNewsUseCase(
        newsRepository
    )
    private val getNewsByIdUseCase = GetNewsByIdUseCase(
        newsRepository
    )
    private val addNewsUseCase = AddNewsUseCase(
        newsRepository,
        authRepository
    )
    private val updateNewsUseCase = UpdateNewsUseCase(
        newsRepository,
        authRepository,
    )

    init {
        observeNewsUseCase().onEach { newsResult ->
            when (newsResult) {
                is Result.Success<NewsEntity> -> {
                    _state.emit(
                        NewsEditorState.Success(
                            newsResult.data
                        )
                    )
                }
                is Result.Error -> {
                    _state.value = NewsEditorState.Error(
                        newsResult.exception.message ?: "Unknown error"
                    )
                }
                Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getNewsById(id: Int) {
        viewModelScope.launch {
            getNewsByIdUseCase(id)
        }
    }

    fun addNews(title: String?, content: String?, image: Uri?) {
        viewModelScope.launch {
            if (title.isNullOrBlank() || content.isNullOrBlank()) {
                _state.emit(NewsEditorState.Error("Title and content cannot be empty"))
                return@launch
            }
            try {
                addNewsUseCase(
                    title,
                    content,
                    image?.let { getFileNameFromUri(it) },
                    image?.let { getFileDataFromUri(it) },
                )
                _state.emit(NewsEditorState.Finished)
            } catch (e: Exception) {
                _state.emit(NewsEditorState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun updateNews(newsId: Int, title: String?, content: String?, image: Uri?) {
        viewModelScope.launch {
            if (title.isNullOrBlank() || content.isNullOrBlank()) {
                _state.emit(NewsEditorState.Error("Title and content cannot be empty"))
                return@launch
            }
            try {
                updateNewsUseCase(
                    newsId,
                    title,
                    content,
                    image?.let { getFileNameFromUri(it) },
                    image?.let { getFileDataFromUri(it) },
                )
                Log.d("NewsEditorActivity", "Updated news with ID: $newsId")
                _state.emit(NewsEditorState.Finished)
            } catch (e: Exception) {
                _state.emit(NewsEditorState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        return try {
            application.contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(
                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                    ))
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileDataFromUri(uri: Uri): ByteArray? {
        return try {
            application.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            null
        }
    }

}

sealed interface NewsEditorState {
    data object Initial : NewsEditorState
    data class Success(val news: NewsEntity) : NewsEditorState
    data object Finished : NewsEditorState
    data class Error(val message: String) : NewsEditorState
}