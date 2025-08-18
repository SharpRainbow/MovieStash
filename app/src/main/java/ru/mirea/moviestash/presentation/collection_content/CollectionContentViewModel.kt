package ru.mirea.moviestash.presentation.collection_content

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.CollectionRepositoryImpl
import ru.mirea.moviestash.data.ContentRepositoryImpl
import ru.mirea.moviestash.data.GenreRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.domain.entities.GenreEntity
import ru.mirea.moviestash.domain.usecases.collection.DeleteContentFromCollectionUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentByGenreUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentFromPublicCollectionUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentFromUserCollectionUseCase
import ru.mirea.moviestash.domain.usecases.genre.GetGenreByIdUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetPublicCollectionInfoUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetUserCollectionInfoUseCase
import ru.mirea.moviestash.domain.usecases.user.GetUserIdUseCase

class CollectionContentViewModel(
    private val application: Application,
    private val collectionId: Int,
    private val userId: Int
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(
        CollectionScreenState()
    )
    val state = _state.asStateFlow()

    private val genreRepository = GenreRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val contentRepository = ContentRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val collectionRepository = CollectionRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val getUserCollectionInfoUseCase = GetUserCollectionInfoUseCase(
        collectionRepository,
        authRepository,
    )
    private val getPublicCollectionInfoUseCase = GetPublicCollectionInfoUseCase(
        collectionRepository
    )
    private val getContentByGenreUseCase = GetContentByGenreUseCase(
        contentRepository
    )
    private val getContentFromPublicCollectionUseCase = GetContentFromPublicCollectionUseCase(
        contentRepository
    )
    private val getContentFromUserCollectionUseCase = GetContentFromUserCollectionUseCase(
        contentRepository,
        authRepository,
    )
    private val getGenreByIdUseCase = GetGenreByIdUseCase(
        genreRepository
    )
    private val getUserIdUseCase = GetUserIdUseCase(
        authRepository
    )
    private val deleteContentFromCollectionUseCase = DeleteContentFromCollectionUseCase(
        collectionRepository,
        authRepository
    )
    private val refreshContentFlow = MutableSharedFlow<Unit>(1)
    val collectionContentFlow =
        refreshContentFlow
            .onStart {
                emit(Unit)
            }
            .flatMapLatest {
                if (userId == -1) {
                    getContentByGenreUseCase(
                        collectionId
                    )
                } else if (userId != 0 && getUserIdUseCase() == userId) {
                    getContentFromUserCollectionUseCase(
                        collectionId
                    )
                } else {
                    getContentFromPublicCollectionUseCase(
                        collectionId
                    )
                }
            }.cachedIn(viewModelScope)

    init {
        getCollectionInfo()
    }

    private fun getCollectionInfo() {
        viewModelScope.launch {
            try {
                if (userId == -1) {
                    val genre = getGenreByIdUseCase(collectionId)
                    _state.update { state ->
                        state.copy(
                            collectionInfo = CollectionEntity(
                                id = 0,
                                description = "",
                                name = genre.name,
                                userId = 0
                            )
                        )
                    }
                } else if (userId != 0 && getUserIdUseCase() == userId) {
                    _state.update { state ->
                        state.copy(
                            collectionInfo = getUserCollectionInfoUseCase(collectionId)
                        )
                    }
                } else {
                    _state.update { state ->
                        state.copy(
                            collectionInfo = getPublicCollectionInfoUseCase(collectionId)
                        )
                    }
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

    fun deleteContentFromCollection(contentId: Int) {
        viewModelScope.launch {
            try {
                deleteContentFromCollectionUseCase(
                    collectionId,
                    contentId
                )
                refreshContent()
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

    private fun refreshContent() {
        viewModelScope.launch {
            refreshContentFlow.emit(Unit)
        }
    }

    companion object {

        fun provideFactory(
            application: Application,
            collectionId: Int,
            userId: Int
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CollectionContentViewModel(
                    application,
                    collectionId,
                    userId
                ) as T
            }
        }
    }
}

data class CollectionScreenState(
    val error: Throwable? = null,
    val collectionInfo: CollectionEntity? = null,
    val isAuthor: Boolean = false
)