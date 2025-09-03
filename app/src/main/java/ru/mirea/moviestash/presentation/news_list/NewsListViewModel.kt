package ru.mirea.moviestash.presentation.news_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import ru.mirea.moviestash.domain.usecases.news.GetNewsListUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase
import javax.inject.Inject

class NewsListViewModel @Inject constructor(
    private val getNewsListUseCase: GetNewsListUseCase,
    private val isModeratorUseCase: IsModeratorUseCase,
): ViewModel() {

    val state = isModeratorUseCase()

    val newsList = getNewsListUseCase().cachedIn(viewModelScope)

}