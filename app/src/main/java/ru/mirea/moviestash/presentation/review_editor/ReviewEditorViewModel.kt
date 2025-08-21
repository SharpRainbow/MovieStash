package ru.mirea.moviestash.presentation.review_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.di.ContentIdQualifier
import ru.mirea.moviestash.di.ReviewIdQualifier
import ru.mirea.moviestash.domain.entities.OpinionEntity
import ru.mirea.moviestash.domain.entities.ReviewEntity
import ru.mirea.moviestash.domain.usecases.opinion.GetOpinionsListUseCase
import ru.mirea.moviestash.domain.usecases.review.AddReviewUseCase
import ru.mirea.moviestash.domain.usecases.review.GetReviewByIdUseCase
import ru.mirea.moviestash.domain.usecases.review.UpdateReviewUseCase
import javax.inject.Inject

class ReviewEditorViewModel @Inject constructor(
    @ContentIdQualifier private val contentId: Int,
    @ReviewIdQualifier private val reviewId: Int,
    private val getOpinionsUseCase: GetOpinionsListUseCase,
    private val getReviewByIdUseCase: GetReviewByIdUseCase,
    private val addReviewUseCase: AddReviewUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<ReviewEditorState>(
        ReviewEditorState.Loading
    )
    val state = _state.asStateFlow()

    init {
        if (reviewId > 0) {
            loadReview()
        }
        loadOpinions()
    }

    private fun loadReview() {
        viewModelScope.launch {
            getReviewByIdUseCase(reviewId).onEach { reviewResult ->
                if (reviewResult.isSuccess) {
                    _state.update { state ->
                        if (state is ReviewEditorState.Editing) {
                            state.copy(
                                review = reviewResult.getOrThrow()
                            )
                        } else {
                            ReviewEditorState.Editing(
                                review = reviewResult.getOrThrow()
                            )
                        }
                    }
                } else {
                    _state.update {
                        ReviewEditorState.Error(
                            dataError = true
                        )
                    }
                }
            }.collect()
        }
    }

    private fun loadOpinions() {
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

