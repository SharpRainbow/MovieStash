package ru.mirea.moviestash.presentation.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.domain.entities.ReviewEntity
import ru.mirea.moviestash.domain.entities.UserStarEntity
import ru.mirea.moviestash.domain.usecases.celebrity.GetCastByContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.GetCrewByContentUseCase
import ru.mirea.moviestash.domain.usecases.collection.AddContentToCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetUserCollectionsUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentUseCase
import ru.mirea.moviestash.domain.usecases.review.GetLatestReviewsUseCase
import ru.mirea.moviestash.domain.usecases.stars.GetRatingUseCase
import ru.mirea.moviestash.domain.usecases.stars.RateContentUseCase
import ru.mirea.moviestash.domain.usecases.stars.UpdateRatingUseCase
import ru.mirea.moviestash.domain.usecases.user.GetUserIdUseCase
import ru.mirea.moviestash.domain.usecases.user.IsLoggedInUseCase
import javax.inject.Inject

class ContentViewModel @Inject constructor(
    private val contentId: Int,
    private val getUserCollectionsUseCase: GetUserCollectionsUseCase,
    private val getContentUseCase: GetContentUseCase,
    private val getCastListUseCase: GetCastByContentUseCase,
    private val getCrewListUseCase: GetCrewByContentUseCase,
    private val getLatestReviewsUseCase: GetLatestReviewsUseCase,
    private val getRatingUseCase: GetRatingUseCase,
    private val rateContentUseCase: RateContentUseCase,
    private val updateRatingUseCase: UpdateRatingUseCase,
    private val addToCollectionUseCase: AddContentToCollectionUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ContentScreenState>(
        ContentScreenState()
    )
    val state = _state.asStateFlow()

    private val refreshCollectionFlow = MutableSharedFlow<Unit>(1)
    val userCollections: Flow<PagingData<CollectionEntity>> =
        refreshCollectionFlow
            .flatMapLatest {
                getUserCollectionsUseCase()
            }.cachedIn(viewModelScope)

    init {
        isLoggedIn()
        getContent()
        getCast()
        getCrew()
        if (isLoggedInUseCase()) {
            getRating()
        }
    }

    private fun getContent() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    isLoading = true,
                    error = null
                )
            }
            try {
                _state.update { state ->
                    state.copy(
                        content = getContentUseCase(contentId)
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
            _state.update { state ->
                state.copy(
                    isLoading = false
                )
            }
        }
    }

    private fun getCast() {
        viewModelScope.launch {
            try {
                _state.update { state ->
                    state.copy(
                        castList = getCastListUseCase(
                            contentId,
                            PREVIEW_ITEMS_COUNT
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

    private fun getCrew() {
        viewModelScope.launch {
            try {
                _state.update { state ->
                    state.copy(
                        crewList = getCrewListUseCase(
                            contentId,
                            PREVIEW_ITEMS_COUNT
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

    fun getReviews() {
        viewModelScope.launch {
            try {
                val reviewList = getLatestReviewsUseCase(
                    contentId
                )
                _state.update { state ->
                    state.copy(
                        reviews = reviewList,
                        canAddReview =
                            reviewList.getOrNull(0)?.userId != getUserIdUseCase()
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

    fun rateContent(rating: Int) {
        viewModelScope.launch {
            try {
                val currentState = state.value
                if (currentState.userStar != null) {
                    updateRatingUseCase(
                        currentState.userStar.id,
                        rating
                    )
                } else {
                    rateContentUseCase(
                        contentId,
                        rating
                    )
                }
                if (rating == 0) {
                    _state.update { state ->
                        state.copy(
                            userStar = null
                        )
                    }
                }
                getContent()
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

    private fun getRating() {
        viewModelScope.launch {
            getRatingUseCase(contentId).onEach { rating ->
                _state.update { state ->
                    state.copy(
                        userStar = rating
                    )
                }
            }.catch { error ->
                _state.update { state ->
                    state.copy(
                        error = error
                    )
                }
            }.collect()
        }
    }

    fun addToCollection(collectionId: Int) {
        viewModelScope.launch {
            try {
                addToCollectionUseCase(
                    collectionId,
                    contentId,
                )
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        error = e
                    )
                }
            }
        }
    }

    fun refreshCollections() {
        viewModelScope.launch {
            refreshCollectionFlow.emit(Unit)
        }
    }

    private fun isLoggedIn() {
        _state.update { state ->
            state.copy(
                isLoggedIn = isLoggedInUseCase()
            )
        }
    }

    companion object {

        private const val PREVIEW_ITEMS_COUNT = 5
    }

}

data class ContentScreenState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val content: ContentEntity? = null,
    val castList: List<CelebrityInContentEntity> = emptyList(),
    val crewList: List<CelebrityInContentEntity> = emptyList(),
    val reviews: List<ReviewEntity> = emptyList(),
    val isLoggedIn: Boolean = false,
    val canAddReview: Boolean = false,
    val userStar: UserStarEntity? = null
)