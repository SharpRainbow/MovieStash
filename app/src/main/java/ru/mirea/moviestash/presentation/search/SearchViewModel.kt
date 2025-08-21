package ru.mirea.moviestash.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.domain.usecases.celebrity.SearchCelebrityUseCase
import ru.mirea.moviestash.domain.usecases.content.SearchContentUseCase
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchCelebrityUseCase: SearchCelebrityUseCase,
    private val searchContentUseCase: SearchContentUseCase
): ViewModel() {

    private val _state = MutableStateFlow(
        SearchScreenState()
    )
    val state = _state.asStateFlow()
    private val searchFlow = MutableStateFlow<String>("")

    val pagedCelebrityList: Flow<PagingData<CelebrityEntityBase>> =
        searchFlow
            .filter {
                state.value.currentTab == SearchTab.CELEBRITY
            }
            .debounce(500)
            .map {
                it.trim()
            }
            .flatMapLatest { input ->
                if (input.isBlank()) {
                    flowOf(PagingData.empty())
                } else {
                    searchCelebrityUseCase(input)
                }
            }.cachedIn(viewModelScope)

    val pagedContentList: Flow<PagingData<ContentEntityBase>> =
        searchFlow
            .filter {
                state.value.currentTab == SearchTab.CONTENT
            }
            .debounce(500)
            .map {
                it.trim()
            }
            .flatMapLatest { input ->
                if (input.isBlank()) {
                    flowOf(PagingData.empty())
                } else {
                    searchContentUseCase(input)
                }
            }.cachedIn(viewModelScope)

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
    val currentTab: SearchTab = SearchTab.CONTENT
)

enum class SearchTab(val tabId: Int) {
    CONTENT(0), CELEBRITY(1)
}