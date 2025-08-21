package ru.mirea.moviestash.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.ReviewEntity
import ru.mirea.moviestash.domain.usecases.review.DeleteReviewUseCase
import ru.mirea.moviestash.domain.usecases.review.GetReviewByIdUseCase
import ru.mirea.moviestash.domain.usecases.user.BanUserUseCase
import ru.mirea.moviestash.domain.usecases.user.GetUserIdUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase
import javax.inject.Inject

class ReviewViewModel @Inject constructor(
    private val reviewId: Int,
    private val getReviewByIdUseCase: GetReviewByIdUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val banUserUseCase: BanUserUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val isModeratorUseCase: IsModeratorUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ReviewScreenState>(
        ReviewScreenState.Initial
    )
    val state = _state.asStateFlow()

    init {
        getReviewByIdUseCase(reviewId)
            .onEach { reviewResult ->
                if (reviewResult.isSuccess) {
                    val review = reviewResult.getOrThrow()
                    _state.update {
                        ReviewScreenState.Loaded(
                            review = review,
                            isAuthor = review.userId == getUserIdUseCase(),
                            isModerator = isModeratorUseCase()
                        )
                    }
                } else {
                    _state.update { state ->
                        if (state is ReviewScreenState.Loaded) {
                            state.copy(
                                dataError = true
                            )
                        } else {
                            state
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun deleteReview() {
        viewModelScope.launch {
            try {
                deleteReviewUseCase(
                    reviewId
                )
                _state.update {
                    ReviewScreenState.Deleted
                }
            } catch (e: Exception) {
                _state.update { state ->
                    if (state is ReviewScreenState.Loaded) {
                        state.copy(
                            dataError = true
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun banUser(userId: Int, banReason: String?) {
        if (banReason.isNullOrBlank()) {
            _state.update { state ->
                if (state is ReviewScreenState.Loaded) {
                    state.copy(
                        errorInputReason = true
                    )
                } else {
                    state
                }
            }
            return
        }
        viewModelScope.launch {
            try {
                banUserUseCase(userId, banReason)
            } catch (e: Exception) {
                _state.update { state ->
                    if (state is ReviewScreenState.Loaded) {
                        state.copy(
                            dataError = true
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun resetErrorInputReason() {
        _state.update { state ->
            if (state is ReviewScreenState.Loaded) {
                state.copy(
                    errorInputReason = false
                )
            } else {
                state
            }
        }
    }

}

sealed interface ReviewScreenState {

    data object Initial : ReviewScreenState

    data class Loaded(
        val review: ReviewEntity? = null,
        val isAuthor: Boolean = false,
        val isModerator: Boolean = false,
        val dataError: Boolean = false,
        val errorInputReason: Boolean = false
    ) : ReviewScreenState

    data object Deleted : ReviewScreenState
}