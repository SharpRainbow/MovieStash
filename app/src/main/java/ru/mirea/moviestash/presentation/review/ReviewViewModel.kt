package ru.mirea.moviestash.presentation.review

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
import ru.mirea.moviestash.data.AuthRepositoryImpl
import ru.mirea.moviestash.data.ReviewRepositoryImpl
import ru.mirea.moviestash.data.UserRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.ReviewEntity
import ru.mirea.moviestash.domain.usecases.review.DeleteReviewUseCase
import ru.mirea.moviestash.domain.usecases.review.GetReviewByIdUseCase
import ru.mirea.moviestash.domain.usecases.review.ObserveReviewUseCase
import ru.mirea.moviestash.domain.usecases.user.BanUserUseCase
import ru.mirea.moviestash.domain.usecases.user.GetUserIdUseCase
import ru.mirea.moviestash.domain.usecases.user.IsModeratorUseCase

class ReviewViewModel(
    private val reviewId: Int,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ReviewScreenState>(
        ReviewScreenState.Initial
    )
    val state = _state.asStateFlow()

    private val reviewRepository = ReviewRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val userRepository = UserRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeReviewViewModel = ObserveReviewUseCase(
        reviewRepository
    )
    private val getReviewByIdUseCase = GetReviewByIdUseCase(
        reviewRepository
    )
    private val isModeratorUseCase = IsModeratorUseCase(
        authRepository
    )
    private val getUserIdUseCase = GetUserIdUseCase(
        authRepository
    )
    private val deleteReviewUseCase = DeleteReviewUseCase(
        reviewRepository,
        authRepository
    )
    private val banUserUseCase = BanUserUseCase(
        userRepository,
        authRepository
    )

    init {
        observeReviewViewModel()
            .onEach { reviewResult ->
                when (reviewResult) {
                    is Result.Error -> {
                        _state.update {
                            ReviewScreenState.Loaded(
                                dataError = true
                            )
                        }
                    }

                    is Result.Success<ReviewEntity> -> {
                        _state.update {
                            ReviewScreenState.Loaded(
                                review = reviewResult.data,
                                isAuthor = reviewResult.data.userId == getUserIdUseCase(),
                                isModerator = isModeratorUseCase()
                            )
                        }
                    }

                    Result.Empty -> {

                    }
                }

            }
            .launchIn(viewModelScope)
    }

    fun loadReview() {
        viewModelScope.launch {
            getReviewByIdUseCase(reviewId)
        }
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

    companion object {

        fun provideFactory(reviewId: Int, application: Application) =
            object : ViewModelProvider.Factory {

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReviewViewModel(reviewId, application) as T
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