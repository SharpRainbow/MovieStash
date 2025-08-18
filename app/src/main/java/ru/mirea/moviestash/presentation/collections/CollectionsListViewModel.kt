package ru.mirea.moviestash.presentation.collections

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.CollectionRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.usecases.collection.GetPublicCollectionsUseCase
import ru.mirea.moviestash.domain.usecases.collection.HideCollectionUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase

class CollectionsListViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(
        CollectionsListScreenState()
    )
    val state = _state.asStateFlow()

    private val collectionsRepository = CollectionRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val getPublicCollectionsUseCase = GetPublicCollectionsUseCase(
        collectionsRepository
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val isModeratorUseCase = IsModeratorUseCase(
        authRepository
    )
    private val hideCollectionUseCase = HideCollectionUseCase(
        collectionsRepository,
        authRepository
    )
    private val refreshCollectionsFlow = MutableSharedFlow<Unit>(1)
    val collectionsFlow =
        refreshCollectionsFlow
            .onStart {
                emit(Unit)
            }
            .flatMapLatest {
                getPublicCollectionsUseCase()
            }.cachedIn(viewModelScope)

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

    fun isModerator() {
        _state.update {
            it.copy(
                isModerator = isModeratorUseCase()
            )
        }
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