package ru.mirea.moviestash.presentation.collection_content

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
import ru.mirea.moviestash.domain.usecases.collection.ObserveCollectionInfoUseCase
import ru.mirea.moviestash.domain.usecases.content.ObserveContentsUseCase
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
    private val observeContentListUseCase = ObserveContentsUseCase(
        contentRepository
    )
    private val observeCollectionInfoUseCase = ObserveCollectionInfoUseCase(
        collectionRepository
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
    private var page = FIRST_PAGE
    private val limit = 20

    init {
        observeCollectionInfoUseCase().onEach { collectionInfoResult ->
            when(collectionInfoResult) {
                Result.Empty -> {}
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            error = collectionInfoResult.exception
                        )
                    }
                }
                is Result.Success<CollectionEntity> -> {
                    _state.update { state ->
                        state.copy(
                            collectionInfo = collectionInfoResult.data,
                            isAuthor = userId == getUserIdUseCase()
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
        observeContentListUseCase().onEach { contentListResult ->
            when(contentListResult) {
                Result.Empty -> {}
                is Result.Error -> {

                }
                is Result.Success<List<ContentEntityBase>> -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = null,
                            collections = state.collections + contentListResult.data
                        )
                    }
                    if (contentListResult.data.isNotEmpty())
                        page++
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getCollectionInfo() {
        viewModelScope.launch {
            if (userId == -1) {
                val genre = getGenreByIdUseCase(collectionId)
                when(genre) {
                    Result.Empty -> {}
                    is Result.Error -> {
                        _state.update { state ->
                            state.copy(
                                error = genre.exception
                            )
                        }
                    }
                    is Result.Success<GenreEntity> -> {
                        _state.update { state ->
                            state.copy(
                                collectionInfo = CollectionEntity(
                                    id = 0,
                                    description = "",
                                    name = genre.data.name,
                                    userId = 0
                                )
                            )
                        }
                    }
                }
            } else if (userId != 0 && getUserIdUseCase() == userId) {
                getUserCollectionInfoUseCase(collectionId)
            } else {
                getPublicCollectionInfoUseCase(collectionId)
            }
        }
    }

    fun getCollectionContents() {
        viewModelScope.launch {
            if (userId == -1) {
                getContentByGenreUseCase(
                    collectionId,
                    page,
                    limit
                )
            } else if (userId != 0 && getUserIdUseCase() == userId) {
                getContentFromUserCollectionUseCase(
                    collectionId,
                    page,
                    limit
                )
            } else {
                getContentFromPublicCollectionUseCase(
                    collectionId,
                    page,
                    limit
                )
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
                _state.update { state ->
                    state.copy(
                        collections = state.collections.filterNot {
                            it.id == contentId
                        }
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

    companion object {

        private const val FIRST_PAGE = 1

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
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val collectionInfo: CollectionEntity? = null,
    val collections: List<ContentEntityBase> = emptyList(),
    val isAuthor: Boolean = false
)