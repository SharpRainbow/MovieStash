package ru.mirea.moviestash.presentation.review_editor

import android.app.Application
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
import ru.mirea.moviestash.data.OpinionRepositoryImpl
import ru.mirea.moviestash.data.ReviewRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.domain.entities.OpinionEntity
import ru.mirea.moviestash.domain.entities.ReviewEntity
import ru.mirea.moviestash.domain.usecases.opinion.GetOpinionsListUseCase
import ru.mirea.moviestash.domain.usecases.review.AddReviewUseCase
import ru.mirea.moviestash.domain.usecases.review.GetReviewByIdUseCase
import ru.mirea.moviestash.domain.usecases.review.ObserveReviewUseCase
import ru.mirea.moviestash.domain.usecases.review.UpdateReviewUseCase

class ReviewEditorViewModel(
    private val application: Application,
    private val contentId: Int
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ReviewEditorState>(
        ReviewEditorState.Loading
    )
    val state = _state.asStateFlow()

    private val opinionRepository = OpinionRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val getOpinionsUseCase = GetOpinionsListUseCase(
        opinionRepository
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val reviewRepository = ReviewRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val addReviewUseCase = AddReviewUseCase(
        reviewRepository,
        authRepository,
    )
    private val observeReviewUseCase = ObserveReviewUseCase(
        reviewRepository
    )
    private val getReviewByIdUseCase = GetReviewByIdUseCase(
        reviewRepository
    )
    private val updateReviewUseCase = UpdateReviewUseCase(
        reviewRepository,
        authRepository,
    )

    init {
        viewModelScope.launch {
            try {
                _state.update { state ->
                    if (state is ReviewEditorState.Editing) {
                        state.copy(
                            opinions = getOpinionsUseCase()
                        )
                    } else {
                        ReviewEditorState.Editing(
                            opinions = getOpinionsUseCase()
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    ReviewEditorState.Error(
                        dataError = true
                    )
                }
            }
        }
        observeReviewUseCase().onEach { reviewResult ->
            when (reviewResult) {
                Result.Empty -> {}
                is Result.Error -> {
                    _state.update {
                        ReviewEditorState.Error(
                            dataError = true
                        )
                    }
                }
                is Result.Success<ReviewEntity> -> {
                    _state.update { state ->
                        if (state is ReviewEditorState.Editing) {
                            state.copy(
                                review = reviewResult.data
                            )
                        } else {
                            ReviewEditorState.Editing(
                                review = reviewResult.data,
                            )
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadReview(reviewId: Int) {
        _state.update { ReviewEditorState.Loading }
        viewModelScope.launch {
            getReviewByIdUseCase(reviewId)
        }
    }

    fun addReview(
        title: String?,
        text: String?,
        opinionId: Int
    ) {
        if (title.isNullOrBlank() || text.isNullOrBlank()) {
            _state.update {
                ReviewEditorState.Error(
                    errorInputTitle = title.isNullOrBlank(),
                    errorInputText = text.isNullOrBlank()
                )
            }
            return
        }
        _state.update { ReviewEditorState.Loading }
        viewModelScope.launch {
            try {
                addReviewUseCase(contentId, title, text, opinionId)
                _state.update { ReviewEditorState.Finished }
            } catch (e: Exception) {
                _state.update {
                    ReviewEditorState.Error(
                        dataError = true
                    )
                }
            }
        }
    }

    fun updateReview(
        reviewId: Int,
        title: String?,
        text: String?,
        opinionId: Int
    ) {
        if (title.isNullOrBlank() || text.isNullOrBlank()) {
            _state.update {
                ReviewEditorState.Error(
                    errorInputTitle = title.isNullOrBlank(),
                    errorInputText = text.isNullOrBlank()
                )
            }
            return
        }
        _state.update { ReviewEditorState.Loading }
        viewModelScope.launch {
            try {
                updateReviewUseCase(reviewId, title, text, opinionId)
                _state.update { ReviewEditorState.Finished }
            } catch (e: Exception) {
                _state.update {
                    ReviewEditorState.Error(
                        dataError = true
                    )
                }
            }
        }
    }

    fun resetErrorInputTitle() {
        _state.update { state ->
            if (state is ReviewEditorState.Error) {
                state.copy(errorInputTitle = false)
            } else {
                state
            }
        }
    }

    fun resetErrorInputText() {
        _state.update { state ->
            if (state is ReviewEditorState.Error) {
                state.copy(errorInputText = false)
            } else {
                state
            }
        }
    }

    companion object {

        fun provideFactory(
            application: Application,
            contentId: Int
        ) = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReviewEditorViewModel(application, contentId) as T
            }
        }
    }
}

sealed interface ReviewEditorState {
    data object Loading : ReviewEditorState
    data class Editing(
        val review: ReviewEntity? = null,
        val opinions: List<OpinionEntity> = emptyList()
    ) : ReviewEditorState
    data object Finished : ReviewEditorState
    data class Error(
        val dataError: Boolean = false,
        val errorInputTitle: Boolean = false,
        val errorInputText: Boolean = false,
    ) : ReviewEditorState
}

