package ru.mirea.moviestash.presentation.review_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import ru.mirea.moviestash.domain.usecases.review.GetReviewsUseCase
import javax.inject.Inject

class ReviewListViewModel @Inject constructor(
    private val contentId: Int,
    private val getReviewsUseCase: GetReviewsUseCase
) : ViewModel() {

    val reviewList =
        getReviewsUseCase(contentId)
            .cachedIn(viewModelScope)

}