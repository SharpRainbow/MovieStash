package ru.mirea.moviestash.presentation.celebrity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.usecases.celebrity.GetCelebrityByIdUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentByCelebrityUseCase
import javax.inject.Inject

class CelebrityViewModel @Inject constructor(
    private val celebrityId: Int,
    private val getCelebrityUseCase: GetCelebrityByIdUseCase,
    private val getContentByCelebrityUseCase: GetContentByCelebrityUseCase
): ViewModel() {

    private val _state = MutableStateFlow<CelebrityScreenState>(
        CelebrityScreenState()
    )
    val state = _state.asStateFlow()

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

}

data class CelebrityScreenState(
    val error: Throwable? = null,
    val celebrity: CelebrityEntity? = null
)