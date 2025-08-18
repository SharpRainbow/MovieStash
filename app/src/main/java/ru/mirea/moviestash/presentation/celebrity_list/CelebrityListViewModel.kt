package ru.mirea.moviestash.presentation.celebrity_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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
import ru.mirea.moviestash.domain.usecases.celebrity.GetPagedCastByContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.GetPagedCrewByContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.ObserveCastListUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.ObserveCrewListUseCase

class PersonListViewModel(
    private val contentId: Int,
    private val actors: Boolean,
) : ViewModel() {

    private val celebrityRepository = CelebrityRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val getPagedCastByContentUseCase = GetPagedCastByContentUseCase(
        celebrityRepository
    )
    private val getPagedCrewByContentUseCase = GetPagedCrewByContentUseCase(
        celebrityRepository
    )

    val celebrityFlow =
        if (actors) {
            getPagedCastByContentUseCase(contentId).cachedIn(viewModelScope)
        } else {
            getPagedCrewByContentUseCase(contentId).cachedIn(viewModelScope)
        }

    companion object {

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