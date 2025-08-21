package ru.mirea.moviestash.presentation.celebrity_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import ru.mirea.moviestash.domain.usecases.celebrity.GetPagedCastByContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.GetPagedCrewByContentUseCase
import javax.inject.Inject

class CelebrityListViewModel @Inject constructor(
    private val contentId: Int,
    private val actors: Boolean,
    private val getPagedCastByContentUseCase: GetPagedCastByContentUseCase,
    private val getPagedCrewByContentUseCase: GetPagedCrewByContentUseCase
) : ViewModel() {

    val celebrityFlow =
        if (actors) {
            getPagedCastByContentUseCase(contentId)
        } else {
            getPagedCrewByContentUseCase(contentId)
        }.cachedIn(viewModelScope)
}