package ru.mirea.moviestash.presentation.celebrity_list

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
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.domain.usecases.celebrity.GetCastByContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.GetCrewByContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.ObserveCastListUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.ObserveCrewListUseCase

class PersonListViewModel(
    private val contentId: Int,
    private val actors: Boolean,
) : ViewModel() {

    private val _state = MutableStateFlow<PersonListState>(
        PersonListState()
    )
    val state = _state.asStateFlow()


    private val celebrityRepository = CelebrityRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeCrewListUseCase = ObserveCrewListUseCase(
        celebrityRepository
    )
    private val observeCastListUseCase = ObserveCastListUseCase(
        celebrityRepository
    )
    private val getCrewListUseCase = GetCrewByContentUseCase(
        celebrityRepository
    )
    private val getCastListUseCase = GetCastByContentUseCase(
        celebrityRepository
    )
    private var page = FIRST_PAGE
    private var limit = 20

    init {
        val flow =
            if (actors) {
                observeCastListUseCase()
            } else {
                observeCrewListUseCase()
            }
        flow.onEach { castList ->
            when (castList) {
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = castList.exception
                        )
                    }
                }

                is Result.Success<List<CelebrityInContentDto>> -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            celebrityList = state.celebrityList + castList.data,
                            error = null
                        )
                    }
                    if (castList.data.isNotEmpty()) {
                        page++
                    }
                }
                Result.Empty -> {

                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadCelebrityList() {
        if (_state.value.isLoading)
            return
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            if (actors) {
                getCastListUseCase(
                    contentId,
                    page,
                    limit
                )
            } else {
                getCrewListUseCase(
                    contentId,
                    page,
                    limit
                )
            }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    companion object {

        private const val FIRST_PAGE = 1

        fun provideFactory(
            contentId: Int,
            actors: Boolean,
        ) = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PersonListViewModel(contentId, actors) as T
            }
        }
    }

}

data class PersonListState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val celebrityList: List<CelebrityInContentDto> = emptyList()
)