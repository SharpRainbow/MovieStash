package ru.mirea.moviestash.presentation.celebrity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.data.CelebrityRepositoryImpl
import ru.mirea.moviestash.data.ContentRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.usecases.celebrity.GetCelebrityByIdUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentByCelebrityUseCase

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
    private val getCelebrityUseCase = GetCelebrityByIdUseCase(
        celebrityRepository
    )
    private val contentRepository = ContentRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val getContentByCelebrityUseCase = GetContentByCelebrityUseCase(
        contentRepository
    )

    val celebrityContentFlow =
        getContentByCelebrityUseCase(celebrityId).cachedIn(viewModelScope)

    init {
        loadCelebrity()
    }

    fun loadCelebrity() {
        viewModelScope.launch {
            try {
                _state.update { state ->
                    state.copy(
                        celebrity = getCelebrityUseCase(celebrityId)
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
    val error: Throwable? = null,
    val celebrity: CelebrityEntity? = null
)