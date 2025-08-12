package ru.mirea.moviestash.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.AddReviewDto
import ru.mirea.moviestash.data.api.dto.UpdateReviewDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.domain.ReviewRepository
import ru.mirea.moviestash.domain.entities.ReviewEntity

class ReviewRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : ReviewRepository {

    private val _contentReviews = MutableStateFlow<Result<List<ReviewEntity>>>(
        Result.Success(
            emptyList()
        )
    )
    override val contentReviews: Flow<Result<List<ReviewEntity>>>
        get() = _contentReviews.asStateFlow()
    private val _review = MutableStateFlow<Result<ReviewEntity>>(
        Result.Empty
    )
    override val review: Flow<Result<ReviewEntity>>
        get() = _review.asStateFlow()

    override suspend fun getReviewsByContentId(
        contentId: Int,
        page: Int,
        limit: Int,
        preview: Boolean
    ) {
        try {
            _contentReviews.emit(
                Result.Success(
                    movieStashApi.getReviewsByContentId(
                        contentId,
                        page,
                        limit
                    ).toListEntity()
                )
            )
        } catch (e: Exception) {
            _contentReviews.emit(
                Result.Error(
                    e
                )
            )
        }
    }

    override suspend fun getReviewById(reviewId: Int) {
        try {
            val review = movieStashApi.getReviewById(reviewId)
            _review.emit(
                Result.Success(
                    review.toEntity()
                )
            )
        } catch (e: Exception) {
            _review.emit(
                Result.Error(
                    e
                )
            )
        }
    }

    override suspend fun addReview(
        token: String,
        contentId: Int,
        title: String,
        description: String,
        opinionId: Int,
    ) {
        movieStashApi.addReview(
            token, AddReviewDto(
                title = title,
                description = description,
                contentId = contentId,
                opinionId = opinionId
            )
        )
    }

    override suspend fun updateReview(
        token: String,
        title: String,
        description: String,
        reviewId: Int,
        opinion: Int,
    ) {
        movieStashApi.updateReview(
            token, reviewId, UpdateReviewDto(
                title = title,
                description = description,
                opinionId = opinion
            )
        )
        getReviewById(reviewId)
    }

    override suspend fun deleteReview(token: String, reviewId: Int) {
       movieStashApi.deleteReview(token, reviewId)
    }
}