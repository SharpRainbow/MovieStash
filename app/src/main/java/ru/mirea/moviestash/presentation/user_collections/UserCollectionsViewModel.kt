@file:OptIn(ExperimentalCoroutinesApi::class)

package ru.mirea.moviestash.presentation.user_collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.usecases.collection.AddCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.DeleteCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetUserCollectionInfoUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetUserCollectionsUseCase
import ru.mirea.moviestash.domain.usecases.collection.PublishCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.UpdateCollectionUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase
import javax.inject.Inject

class UserCollectionsViewModel @Inject constructor(
    private val getUserCollectionsUseCase: GetUserCollectionsUseCase,
    private val addCollectionUseCase: AddCollectionUseCase,
    private val getUserCollectionInfoUseCase: GetUserCollectionInfoUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val publicCollectionsUseCase: PublishCollectionUseCase,
    private val isModeratorUseCase: IsModeratorUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(
        UserCollectionsScreenState()
    )
    val state = _state.asStateFlow()

    private val refreshCollectionsFlow = MutableSharedFlow<Unit>(1)
    val collectionFlow: Flow<PagingData<CollectionEntity>> =
        refreshCollectionsFlow
            .onStart { emit(Unit) }
            .flatMapLatest {
                getUserCollectionsUseCase()
            }.cachedIn(viewModelScope)

    init {
        isModerator()
    }

    fun addCollection(name: String?, description: String?) {
        if (name.isNullOrBlank()) {
            _state.update { state ->
                state.copy(
                    errorInputName = true
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    isLoading = true,
                    error = null
                )
            }
            try {
                addCollectionUseCase(name, description)
                refreshCollectionsFlow.emit(Unit)
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

    fun resetErrorInputName() {
        _state.update { state ->
            state.copy(
                errorInputName = false,
            )
        }
    }

    fun getCollectionInfo(collectionId: Int) {
        viewModelScope.launch {
            try {
                _state.update { state ->
                    state.copy(
                        modifiedCollection = getUserCollectionInfoUseCase(collectionId)
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

    fun deleteCollection(collectionId: Int) {
        viewModelScope.launch {
            try {
                deleteCollectionUseCase(collectionId)
                refreshCollections()
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

    fun updateCollection(
        collectionId: Int,
        name: String?,
        description: String?
    ) {
        if (name.isNullOrBlank()) {
            _state.update { state ->
                state.copy(
                    errorInputName = true
                )
            }
            return
        }
        viewModelScope.launch {
            try {
                updateCollectionUseCase(collectionId, name, description)
                refreshCollections()
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e
                    )
                }
            }
        }
    }

    fun publishCollection(
        collectionId: Int,
    ) {
        viewModelScope.launch {
            try {
                publicCollectionsUseCase(collectionId)
                refreshCollections()
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e
                    )
                }
            }
        }
    }

    fun refreshCollections() {
        viewModelScope.launch {
            refreshCollectionsFlow.emit(Unit)
        }
    }

    private fun isModerator() {
        _state.update { state ->
            state.copy(
                isModerator = isModeratorUseCase()
            )
        }
    }

}

data class UserCollectionsScreenState(
    val isModerator: Boolean = false,
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val errorInputName: Boolean = false,
    val modifiedCollection: CollectionEntity? = null,
    val dataSaved: Boolean = false
)