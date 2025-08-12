package ru.mirea.moviestash.presentation.collections

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.usecases.collection.GetPublicCollectionsUseCase
import ru.mirea.moviestash.domain.usecases.collection.HideCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.ObserveCollectionsListUseCase
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
    private val observeCollectionsListUseCase = ObserveCollectionsListUseCase(
        collectionsRepository
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
    private var page = FIRST_PAGE
    private val limit = 20

    init {
        getCollections()
        observeCollectionsListUseCase().onEach { collectionsListResult ->
            when(collectionsListResult) {
                Result.Empty -> {}
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = collectionsListResult.exception
                        )
                    }
                }
                is Result.Success<List<CollectionEntity>> -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            collections = state.collections + collectionsListResult.data
                        )
                    }
                    if (collectionsListResult.data.isNotEmpty()) {
                        page++
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getCollections() {
        viewModelScope.launch {
            getPublicCollectionsUseCase(
                page, limit
            )
        }
    }

    fun hideCollection(collectionId: Int) {
        viewModelScope.launch {
            try {
                hideCollectionUseCase(collectionId)
                getCollections()
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

    fun resetPage() {
        page = FIRST_PAGE
        _state.update {
            it.copy(
                collections = emptyList(),
            )
        }
    }

    companion object {
        private const val FIRST_PAGE = 1
    }

}

data class CollectionsListScreenState(
    val isLoading: Boolean = false,
    val isModerator: Boolean = false,
    val error: Exception? = null,
    val collections: List<CollectionEntity> = emptyList()
)