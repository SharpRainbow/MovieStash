package ru.mirea.moviestash.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
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
    private val celebritySearchFlow = MutableStateFlow<String>("")
    private val contentSearchFlow = MutableStateFlow<String>("")

    val pagedCelebrityList: Flow<PagingData<CelebrityEntityBase>> =
        celebritySearchFlow
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
        contentSearchFlow
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
            when (state.value.currentTab) {
                SearchTab.CELEBRITY -> searchCelebrity(searchValue)
                SearchTab.CONTENT -> searchContent(searchValue)
            }
        }
    }

    private fun searchCelebrity(input: String?) {
        input?.let { searchValue ->
            viewModelScope.launch {
                celebritySearchFlow.emit(searchValue)
            }
        }
    }

    private fun searchContent(input: String?) {
        input?.let { searchValue ->
            viewModelScope.launch {
                contentSearchFlow.emit(searchValue)
            }
        }
    }

    fun changeTab(searchTab: SearchTab) {
        _state.update { state ->
            state.copy(
                currentTab = searchTab,
            )
        }
        when (searchTab) {
            SearchTab.CELEBRITY -> searchCelebrity(contentSearchFlow.value)
            SearchTab.CONTENT -> searchContent(celebritySearchFlow.value)
        }
    }

}

data class SearchScreenState(
    val currentTab: SearchTab = SearchTab.CONTENT
)

enum class SearchTab(val tabId: Int) {
    CONTENT(0), CELEBRITY(1)
}