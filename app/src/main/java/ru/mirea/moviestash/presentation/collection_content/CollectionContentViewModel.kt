package ru.mirea.moviestash.presentation.collection_content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.di.CollectionIdQualifier
import ru.mirea.moviestash.di.UserIdQualifier
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.usecases.collection.DeleteContentFromCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetPublicCollectionInfoUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetUserCollectionInfoUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentByGenreUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentFromPublicCollectionUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentFromUserCollectionUseCase
import ru.mirea.moviestash.domain.usecases.genre.GetGenreByIdUseCase
import ru.mirea.moviestash.domain.usecases.user.GetUserIdUseCase
import javax.inject.Inject

class CollectionContentViewModel @Inject constructor(
    @CollectionIdQualifier private val collectionId: Int,
    @UserIdQualifier private val userId: Int,
    private val getContentByGenreUseCase: GetContentByGenreUseCase,
    private val getContentFromUserCollectionUseCase: GetContentFromUserCollectionUseCase,
    private val getContentFromPublicCollectionUseCase: GetContentFromPublicCollectionUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getGenreByIdUseCase: GetGenreByIdUseCase,
    private val getPublicCollectionInfoUseCase: GetPublicCollectionInfoUseCase,
    private val getUserCollectionInfoUseCase: GetUserCollectionInfoUseCase,
    private val deleteContentFromCollectionUseCase: DeleteContentFromCollectionUseCase
): ViewModel() {

    private val _state = MutableStateFlow(
        CollectionScreenState()
    )
    val state = _state.asStateFlow()

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
}

data class CollectionScreenState(
    val error: Throwable? = null,
    val collectionInfo: CollectionEntity? = null,
    val isAuthor: Boolean = false
)