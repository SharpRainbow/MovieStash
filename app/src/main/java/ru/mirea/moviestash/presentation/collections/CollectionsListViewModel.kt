package ru.mirea.moviestash.presentation.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.usecases.collection.GetPublicCollectionsUseCase
import ru.mirea.moviestash.domain.usecases.collection.HideCollectionUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase
import javax.inject.Inject

class CollectionsListViewModel @Inject constructor(
    private val getPublicCollectionsUseCase: GetPublicCollectionsUseCase,
    private val hideCollectionUseCase: HideCollectionUseCase,
    private val isModeratorUseCase: IsModeratorUseCase
): ViewModel() {

    private val _state = MutableStateFlow(
        CollectionsListScreenState()
    )
    val state = _state.asStateFlow()

    private val refreshCollectionsFlow = MutableSharedFlow<Unit>(1)
    val collectionsFlow =
        refreshCollectionsFlow
            .onStart {
                emit(Unit)
            }
            .flatMapLatest {
                getPublicCollectionsUseCase()
            }.cachedIn(viewModelScope)

    init {
        observeRole()
    }

    fun hideCollection(collectionId: Int) {
        viewModelScope.launch {
            try {
                hideCollectionUseCase(collectionId)
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

    private fun observeRole() {
        isModeratorUseCase().onEach { isModerator ->
            _state.update { previousState ->
                previousState.copy(
                    isModerator = isModerator
                )
            }
        }.launchIn(viewModelScope)
    }

    fun refreshCollections() {
        viewModelScope.launch {
            refreshCollectionsFlow.emit(Unit)
        }
    }

}

data class CollectionsListScreenState(
    val isModerator: Boolean = false,
    val error: Exception? = null
)