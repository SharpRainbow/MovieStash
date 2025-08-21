package ru.mirea.moviestash.presentation.news_editor

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.domain.usecases.news.AddNewsUseCase
import ru.mirea.moviestash.domain.usecases.news.GetNewsByIdUseCase
import ru.mirea.moviestash.domain.usecases.news.UpdateNewsUseCase
import java.io.IOException
import javax.inject.Inject

class NewsEditorViewModel @Inject constructor(
    private val newsId: Int,
    private val getNewsByIdUseCase: GetNewsByIdUseCase,
    private val addNewsUseCase: AddNewsUseCase,
    private val updateNewsUseCase: UpdateNewsUseCase,
    private val application: Application
): ViewModel() {

    private val _state = MutableStateFlow<NewsEditorState>(
        NewsEditorState.Initial
    )
    val state = _state.asStateFlow()

    init {
        if (newsId > 0) {
            getNewsById()
        }
    }

    fun getNewsById() {
        getNewsByIdUseCase(newsId)
            .onEach { newsResult ->
                if (newsResult.isSuccess) {
                    _state.update {
                        NewsEditorState.Success(
                            news = newsResult.getOrThrow()
                        )
                    }
                } else {
                    _state.update {
                        NewsEditorState.Error(
                            dataError = true
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun addNews(title: String?, content: String?, image: Uri?) {
        viewModelScope.launch {
            if (title.isNullOrBlank() || content.isNullOrBlank()) {
                _state.emit(
                    NewsEditorState.Error(
                        errorInputTitle = title.isNullOrBlank(),
                        errorInputContent = content.isNullOrBlank()
                    )
                )
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
                _state.emit(
                    NewsEditorState.Error(
                        dataError = true
                    )
                )
            }
        }
    }

    fun updateNews(newsId: Int, title: String?, content: String?, image: Uri?) {
        viewModelScope.launch {
            if (title.isNullOrBlank() || content.isNullOrBlank()) {
                _state.emit(
                    NewsEditorState.Error(
                        errorInputTitle = title.isNullOrBlank(),
                        errorInputContent = content.isNullOrBlank()
                    )
                )
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
                _state.emit(NewsEditorState.Finished)
            } catch (e: Exception) {
                _state.emit(
                    NewsEditorState.Error(
                        dataError = true
                    )
                )
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

    fun resetErrorInputTitle() {
        _state.update { state ->
            if (state is NewsEditorState.Error) {
                state.copy(errorInputTitle = false)
            } else {
                state
            }
        }
    }

    fun resetErrorInputContent() {
        _state.update { state ->
            if (state is NewsEditorState.Error) {
                state.copy(errorInputContent = false)
            } else {
                state
            }
        }
    }

}

sealed interface NewsEditorState {
    data object Initial : NewsEditorState
    data class Success(val news: NewsEntity) : NewsEditorState
    data object Finished : NewsEditorState
    data class Error(
        val dataError: Boolean = false,
        val errorInputTitle: Boolean = false,
        val errorInputContent: Boolean = false,
    ) : NewsEditorState
}