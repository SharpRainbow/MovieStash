package ru.mirea.moviestash.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.data.CelebrityRepositoryImpl
import ru.mirea.moviestash.data.ContentRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.domain.usecases.celebrity.SearchCelebrityUseCase

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        SearchScreenState()
    )
    val state = _state.asStateFlow()
    private val searchFlow = MutableStateFlow<String>("")

    private val contentRepository = ContentRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val celebrityRepository = CelebrityRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val searchCelebrityUseCase = SearchCelebrityUseCase(
        celebrityRepository
    )

    init {
        searchFlow
            .debounce(500)
            .map {
                it.trim()
            }
            .onEach { input ->
                _state.update { state ->
                    if (input.isBlank()) {
                        state.copy(
                            pagedCelebrityList = null,
                            pagedContentList = null
                        )
                    } else {
                        state.copy(
                            pagedCelebrityList =
                                searchCelebrityUseCase(input)
                                .cachedIn(viewModelScope),
                            pagedContentList = contentRepository
                                .getContentSearchResultFlow(input)
                                .cachedIn(viewModelScope)
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun search(input: String?) {
        input?.let { searchValue ->
            viewModelScope.launch {
                searchFlow.emit(searchValue)
            }
        }
    }

    fun changeTab(searchTab: SearchTab) {
        _state.update { state ->
            state.copy(
                currentTab = searchTab,
            )
        }
    }

}

data class SearchScreenState(
    val currentTab: SearchTab = SearchTab.CONTENT,
    val pagedCelebrityList: Flow<PagingData<CelebrityEntityBase>>? = null,
    val pagedContentList: Flow<PagingData<ContentEntityBase>>? = null,
)

enum class SearchTab(val tabId: Int) {
    CONTENT(0), CELEBRITY(1)
}