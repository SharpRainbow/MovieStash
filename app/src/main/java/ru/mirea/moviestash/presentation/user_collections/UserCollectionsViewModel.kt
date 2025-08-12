package ru.mirea.moviestash.presentation.user_collections

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
import ru.mirea.moviestash.domain.usecases.collection.AddCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.DeleteCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetUserCollectionInfoUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetUserCollectionsUseCase
import ru.mirea.moviestash.domain.usecases.collection.ObserveCollectionInfoUseCase
import ru.mirea.moviestash.domain.usecases.collection.ObserveCollectionsListUseCase
import ru.mirea.moviestash.domain.usecases.collection.PublishCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.UpdateCollectionUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase

class UserCollectionsViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(
        UserCollectionsScreenState()
    )
    val state = _state.asStateFlow()

    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val collectionsRepository = CollectionRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeCollectionUseCase = ObserveCollectionInfoUseCase(
        collectionsRepository
    )
    private val observeCollectionsListUseCase = ObserveCollectionsListUseCase(
        collectionsRepository
    )
    private val getUserCollectionsUseCase = GetUserCollectionsUseCase(
        collectionsRepository,
        authRepository,
    )
    private val addCollectionUseCase = AddCollectionUseCase(
        collectionsRepository,
        authRepository
    )
    private val deleteCollectionUseCase = DeleteCollectionUseCase(
        collectionsRepository,
        authRepository
    )
    private val getUserCollectionInfoUseCase = GetUserCollectionInfoUseCase(
        collectionsRepository,
        authRepository
    )
    private val updateCollectionUseCase = UpdateCollectionUseCase(
        collectionsRepository,
        authRepository
    )
    private val publicCollectionsUseCase = PublishCollectionUseCase(
        collectionsRepository,
        authRepository
    )
    private val isModeratorUseCase = IsModeratorUseCase(
        authRepository
    )
    private var page = FIRST_PAGE
    private val limit = 20

    init {
        isModerator()
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
                            error = null,
                            collections = state.collections + collectionsListResult.data
                        )
                    }
                    if (collectionsListResult.data.isNotEmpty()) {
                        page++
                    }
                }
            }
        }.launchIn(viewModelScope)
        observeCollectionUseCase().onEach { collectionResult ->
            when(collectionResult) {
                Result.Empty -> {}
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            modifiedCollection = null,
                            error = collectionResult.exception
                        )
                    }
                }
                is Result.Success<CollectionEntity> -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            modifiedCollection = collectionResult.data,
                            error = null
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getCollections() {
        viewModelScope.launch {
            getUserCollectionsUseCase(
                page, limit
            )
        }
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
                resetState()
                getCollections()
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

    fun resetErrorInputName() {
        _state.update { state ->
            state.copy(
                errorInputName = false,
            )
        }
    }

    fun getCollectionInfo(collectionId: Int) {
        viewModelScope.launch {
            getUserCollectionInfoUseCase(collectionId)
        }
    }

    fun deleteCollection(collectionId: Int) {
        viewModelScope.launch {
            try {
                deleteCollectionUseCase(collectionId)
                updateCollections { collections ->
                    collections.filter { it.id != collectionId }
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
                updateCollections { collections ->
                    collections.map { collection ->
                        if (collection.id == collectionId) {
                            collection.copy(
                                name = name,
                                description = description.orEmpty()
                            )
                        } else {
                            collection
                        }
                    }
                }
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
                updateCollections { collections ->
                    collections.filter { it.id != collectionId }
                }
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

    private fun isModerator() {
        _state.update { state ->
            state.copy(
                isModerator = isModeratorUseCase()
            )
        }
    }

    private inline fun updateCollections(
        transformation: (List<CollectionEntity>) -> List<CollectionEntity>
    ) {
        _state.update { state ->
            state.copy(
                collections = transformation(state.collections)
            )
        }
    }

    private fun resetState() {
        page = FIRST_PAGE
        _state.update { state ->
            state.copy(
                collections = emptyList(),
            )
        }
    }

    companion object {
        private const val FIRST_PAGE = 1
    }

}

data class UserCollectionsScreenState(
    val isModerator: Boolean = false,
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val errorInputName: Boolean = false,
    val collections: List<CollectionEntity> = emptyList(),
    val modifiedCollection: CollectionEntity? = null,
    val dataSaved: Boolean = false
)