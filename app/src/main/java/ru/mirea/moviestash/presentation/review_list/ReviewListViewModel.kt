package ru.mirea.moviestash.presentation.review_list

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
import ru.mirea.moviestash.data.ReviewRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.ReviewEntity
import ru.mirea.moviestash.domain.usecases.review.GetReviewsUseCase
import ru.mirea.moviestash.domain.usecases.review.ObserveReviewsUseCase

class ReviewListViewModel(
    private val contentId: Int
): ViewModel() {

    private val _state = MutableStateFlow<ReviewListState>(
        ReviewListState()
    )
    val state = _state.asStateFlow()

    private val reviewRepository = ReviewRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeReviewsUseCase = ObserveReviewsUseCase(
        reviewRepository
    )
    private val getReviewsUseCase = GetReviewsUseCase(
        reviewRepository
    )
    private var page = FIRST_PAGE
    private val limit = 20

    init {
        observeReviewsUseCase()
            .onEach { reviewList ->
                when(reviewList) {
                    is Result.Error -> {
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                error = state.error
                            )
                        }
                    }
                    is Result.Success<List<ReviewEntity>> -> {
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                reviewList = state.reviewList + reviewList.data
                            )
                        }
                        if (reviewList.data.isNotEmpty())
                            page++
                    }

                    Result.Empty -> {

                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadReviews() {
        if (_state.value.isLoading)
            return
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            getReviewsUseCase(
                contentId,
                page,
                limit,
                false
            )
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    companion object {

        private const val FIRST_PAGE = 1

        fun provideFactory(
            contentId: Int
        ) = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReviewListViewModel(contentId) as T
            }
        }
    }
}

data class ReviewListState(
    val isLoading: Boolean = false,
    val error: Error? = null,
    val reviewList: List<ReviewEntity> = emptyList()
)