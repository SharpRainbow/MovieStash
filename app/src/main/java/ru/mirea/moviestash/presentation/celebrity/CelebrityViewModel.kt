package ru.mirea.moviestash.presentation.celebrity

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
import ru.mirea.moviestash.data.CelebrityRepositoryImpl
import ru.mirea.moviestash.data.ContentRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.domain.usecases.celebrity.GetCelebrityByIdUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentByCelebrityUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.ObserveCelebrityUseCase
import ru.mirea.moviestash.domain.usecases.content.ObserveContentByCelebrityUseCase

class CelebrityViewModel(
    private val celebrityId: Int
): ViewModel() {

    private val _state = MutableStateFlow<CelebrityScreenState>(
        CelebrityScreenState()
    )
    val state = _state.asStateFlow()
    private val celebrityRepository = CelebrityRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeCelebrityUseCase = ObserveCelebrityUseCase(
        celebrityRepository
    )
    private val getCelebrityUseCase = GetCelebrityByIdUseCase(
        celebrityRepository
    )
    private val contentRepository = ContentRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeContentByCelebrityUseCase = ObserveContentByCelebrityUseCase(
        contentRepository
    )
    private val getContentByCelebrityUseCase = GetContentByCelebrityUseCase(
        contentRepository
    )

    private var page = FIRST_PAGE
    private val limit = 20

    init {
        observeCelebrityUseCase()
            .onEach { celebrity ->
                when(celebrity) {
                    is Result.Error -> {
                        _state.update { state ->
                            state.copy(
                                error = celebrity.exception
                            )
                        }
                    }
                    is Result.Success<CelebrityEntity> -> {
                        _state.update { state ->
                            state.copy(
                                celebrity = celebrity.data
                            )
                        }
                    }
                    Result.Empty -> {

                    }
                }
            }
            .launchIn(viewModelScope)
        observeContentByCelebrityUseCase()
            .onEach { contentResult ->
                when(contentResult) {
                    is Result.Error -> {
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                error = contentResult.exception
                            )
                        }
                    }
                    is Result.Success<List<ContentEntityBase>> -> {
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                error = null,
                                contentList = contentResult.data
                            )
                        }
                    }
                    Result.Empty -> {

                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadCelebrity() {
        viewModelScope.launch {
            getCelebrityUseCase(celebrityId)
        }
    }

    fun loadContent() {
        viewModelScope.launch {
            getContentByCelebrityUseCase(
                celebrityId,
                page,
                limit
            )
        }
    }

    companion object {

        private const val FIRST_PAGE = 1

        fun provideFactory(
            celebrityId: Int
        ) = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CelebrityViewModel(celebrityId) as T
            }
        }
    }

}

data class CelebrityScreenState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val celebrity: CelebrityEntity? = null,
    val contentList: List<ContentEntityBase> = emptyList()
)