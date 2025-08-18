package ru.mirea.moviestash.presentation.celebrity_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import ru.mirea.moviestash.data.CelebrityRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.usecases.celebrity.GetPagedCastByContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.GetPagedCrewByContentUseCase

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