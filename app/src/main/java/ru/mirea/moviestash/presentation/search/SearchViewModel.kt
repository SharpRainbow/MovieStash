package ru.mirea.moviestash.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.CelebrityRepositoryImpl
import ru.mirea.moviestash.data.ContentRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.domain.usecases.content.ObserveContentsUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.ObserveCelebrityListUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.SearchCelebrityUseCase
import ru.mirea.moviestash.domain.usecases.content.SearchContentUseCase

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
    private val observeContentListUseCase = ObserveContentsUseCase(
        contentRepository
    )
    private val observeCelebrityListUseCase = ObserveCelebrityListUseCase(
        celebrityRepository
    )
    private val getContentListUseCase = SearchContentUseCase(
        contentRepository
    )
    private val getCelebrityUseCase = SearchCelebrityUseCase(
        celebrityRepository
    )
    private var celebrityPage = FIRST_PAGE
    private var contentPage = FIRST_PAGE
    private val limit = 10

    init {
        searchFlow
            .debounce(500)
            .map {
                it.trim()
            }
            .onEach { input ->
                if (input != state.value.searchQuery) {
                    resetSearch()
                    _state.update { state ->
                        state.copy(
                            searchQuery = input,
                            contentList = emptyList(),
                            celebrityList = emptyList(),
                        )
                    }
                }
                loadMore()
            }
            .launchIn(viewModelScope)
        observeContentListUseCase().onEach { contentListResult ->
            when (contentListResult) {
                Result.Empty -> {}
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = contentListResult.exception
                        )
                    }
                }

                is Result.Success<List<ContentEntityBase>> -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = null,
                            contentList = state.contentList + contentListResult.data
                        )
                    }
                    if (contentListResult.data.isNotEmpty()) {
                        contentPage++
                    }
                }
            }
        }.launchIn(viewModelScope)
        observeCelebrityListUseCase().onEach { celebrityListResult ->
            when (celebrityListResult) {
                Result.Empty -> {}
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = celebrityListResult.exception
                        )
                    }
                }

                is Result.Success<List<CelebrityEntityBase>> -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = null,
                            celebrityList = state.celebrityList + celebrityListResult.data
                        )
                    }
                    if (celebrityListResult.data.isNotEmpty()) {
                        celebrityPage++
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun search(input: String?) {
        input?.let { searchValue ->
            viewModelScope.launch {
                searchFlow.emit(searchValue)
            }
        }
    }

    fun loadMore() {
        val query = state.value.searchQuery
        val isContentTab = state.value.currentTab == SearchTab.CONTENT
        if (query.isBlank() || state.value.isLoading)
            return
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            if (isContentTab) {
                getContentListUseCase(
                    query,
                    contentPage,
                    limit
                )
            } else {
                getCelebrityUseCase(
                    query,
                    celebrityPage,
                    limit
                )
            }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun changeTab(searchTab: SearchTab) {
        _state.update { state ->
            state.copy(
                currentTab = searchTab,
            )
        }
        loadMore()
    }

    private fun resetSearch() {
        celebrityPage = FIRST_PAGE
        contentPage = FIRST_PAGE
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}

data class SearchScreenState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val currentTab: SearchTab = SearchTab.CONTENT,
    val searchQuery: String = "",
    val celebrityList: List<CelebrityEntityBase> = emptyList(),
    val contentList: List<ContentEntityBase> = emptyList()
)

enum class SearchTab(val tabId: Int) {
    CONTENT(0), CELEBRITY(1)
}