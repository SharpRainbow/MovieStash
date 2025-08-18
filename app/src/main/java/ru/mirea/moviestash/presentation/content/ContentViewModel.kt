package ru.mirea.moviestash.presentation.content

import android.app.Application
import android.util.Log
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
import ru.mirea.moviestash.data.CelebrityRepositoryImpl
import ru.mirea.moviestash.data.CollectionRepositoryImpl
import ru.mirea.moviestash.data.ContentRepositoryImpl
import ru.mirea.moviestash.data.ReviewRepositoryImpl
import ru.mirea.moviestash.data.UserStarRepositoryImpl
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.domain.entities.ReviewEntity
import ru.mirea.moviestash.domain.entities.UserStarEntity
import ru.mirea.moviestash.domain.usecases.celebrity.GetCastByContentUseCase
import ru.mirea.moviestash.domain.usecases.content.GetContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.GetCrewByContentUseCase
import ru.mirea.moviestash.domain.usecases.review.GetReviewsUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.ObserveCastListUseCase
import ru.mirea.moviestash.domain.usecases.content.ObserveContentUseCase
import ru.mirea.moviestash.domain.usecases.celebrity.ObserveCrewListUseCase
import ru.mirea.moviestash.domain.usecases.collection.AddContentToCollectionUseCase
import ru.mirea.moviestash.domain.usecases.collection.GetUserCollectionsUseCase
import ru.mirea.moviestash.domain.usecases.collection.ObserveCollectionsListUseCase
import ru.mirea.moviestash.domain.usecases.review.ObserveReviewsUseCase
import ru.mirea.moviestash.domain.usecases.stars.GetRatingUseCase
import ru.mirea.moviestash.domain.usecases.stars.ObserveRatingUseCase
import ru.mirea.moviestash.domain.usecases.stars.RateContentUseCase
import ru.mirea.moviestash.domain.usecases.stars.UpdateRatingUseCase
import ru.mirea.moviestash.domain.usecases.user.IsLoggedInUseCase

class ContentViewModel(
    private val contentId: Int,
    private val application: Application
) : ViewModel() {

    private val contentRepository = ContentRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val celebrityRepository = CelebrityRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val reviewRepository = ReviewRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val userStarRepository = UserStarRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val authRepository = AuthRepositoryImpl(
        application,
        ApiProvider.movieStashApi
    )
    private val collectionRepository = CollectionRepositoryImpl(
        ApiProvider.movieStashApi
    )
    private val observeContentUseCase = ObserveContentUseCase(
        contentRepository
    )
    private val observeCastListUseCase = ObserveCastListUseCase(
        celebrityRepository
    )
    private val observeCrewListUseCase = ObserveCrewListUseCase(
        celebrityRepository
    )
    private val observeReviewsUseCase = ObserveReviewsUseCase(
        reviewRepository
    )
    private val getContentUseCase = GetContentUseCase(
        contentRepository
    )
    private val getCrewListUseCase = GetCrewByContentUseCase(
        celebrityRepository
    )
    private val getCastListUseCase = GetCastByContentUseCase(
        celebrityRepository
    )
    private val getReviewUseCase = GetReviewsUseCase(
        reviewRepository
    )
    private val observeRatingUseCase = ObserveRatingUseCase(
        userStarRepository
    )
    private val getRatingUseCase = GetRatingUseCase(
        userStarRepository,
        authRepository
    )
    private val rateContentUseCase = RateContentUseCase(
        userStarRepository,
        authRepository
    )
    private val updateRatingUseCase = UpdateRatingUseCase(
        userStarRepository,
        authRepository
    )
    private val observeCollectionsUseCase = ObserveCollectionsListUseCase(
        collectionRepository
    )
    private val getUserCollectionsUseCase = GetUserCollectionsUseCase(
        collectionRepository,
        authRepository
    )
    private val addToCollectionUseCase = AddContentToCollectionUseCase(
        collectionRepository,
        authRepository
    )
    private val isLoggedInUseCase = IsLoggedInUseCase(
        authRepository
    )
    private var collectionPage = FIRST_PAGE

    private val _state = MutableStateFlow<ContentScreenState>(
        ContentScreenState()
    )
    val state = _state.asStateFlow()

    init {
        isLoggedIn()
        getContent()
        getCast()
        getCrew()
        getRating()
        observeRatingUseCase().onEach { ratingResult ->
            when (ratingResult) {
                is Result.Success -> {
                    _state.update { state ->
                        state.copy(
                            userStar = ratingResult.data
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { state ->
                        if (state.isLoggedIn) {
                            state.copy(
                                isLoading = false,
                                error = ratingResult.exception
                            )
                        } else {
                            state
                        }
                    }
                }
                Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
        observeContentUseCase().onEach { contentResult ->
            when (contentResult) {
                is Result.Success -> {
                    _state.update { state ->
                        state.copy(
                            content = contentResult.data
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = contentResult.exception
                        )
                    }
                }
                Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
        observeCrewListUseCase().onEach { crewResult ->
            when (crewResult) {
                is Result.Success -> {
                    _state.update { state ->
                        state.copy(
                            crewList = crewResult.data
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = crewResult.exception
                        )
                    }
                }
                Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
        observeCastListUseCase().onEach { castResult ->
            when (castResult) {
                is Result.Success -> {
                    _state.update { state ->
                        state.copy(
                            castList = castResult.data
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = castResult.exception
                        )
                    }
                }
                Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
        observeReviewsUseCase().onEach { reviewsResult ->
            when (reviewsResult) {
                is Result.Success -> {
                    _state.update { state ->
                        state.copy(
                            reviews = reviewsResult.data
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = reviewsResult.exception
                        )
                    }
                }
                Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
        observeCollectionsUseCase().onEach { collectionResult ->
            when (collectionResult) {
                is Result.Success -> {
                    _state.update { state ->
                        state.copy(
                            userCollections = state.userCollections + collectionResult.data
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = collectionResult.exception
                        )
                    }
                }
                Result.Empty -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getContent() {
        viewModelScope.launch {
            getContentUseCase(contentId)
        }
    }

    fun getCast() {
        viewModelScope.launch {
            getCastListUseCase(
                contentId,
                FIRST_PAGE,
                5
            )
        }
    }

    fun getCrew() {
        viewModelScope.launch {
            getCrewListUseCase(
                contentId,
                FIRST_PAGE,
                5
            )
        }
    }

    fun getReviews() {
        viewModelScope.launch {
            getReviewUseCase(
                contentId,
                FIRST_PAGE,
                5,
                true
            )
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
                } else {
                    getRating()
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
            getRatingUseCase(contentId)
        }
    }

    fun getUserCollections() {
        viewModelScope.launch {
            try {
                getUserCollectionsUseCase(
                    collectionPage,
                    LIMIT
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

    fun resetCollectionPage() {
        collectionPage = FIRST_PAGE
        _state.update { state ->
            state.copy(
                userCollections = emptyList()
            )
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

    private fun isLoggedIn() {
        _state.update { state ->
            state.copy(
                isLoggedIn = isLoggedInUseCase()
            )
        }
    }

    companion object {

        private const val FIRST_PAGE = 1
        private const val LIMIT = -1

        fun provideFactory(contentId: Int, application: Application) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContentViewModel(contentId, application) as T
                }
            }
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
    val userStar: UserStarEntity? = null,
    val userCollections: List<CollectionEntity> = emptyList(),
)