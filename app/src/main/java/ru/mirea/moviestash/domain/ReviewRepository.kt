package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.domain.entities.ReviewEntity

interface ReviewRepository {

    val contentReviews: Flow<Result<List<ReviewEntity>>>

    val review: Flow<Result<ReviewEntity>>

    suspend fun getReviewsByContentId(
        contentId: Int,
        page: Int,
        limit: Int,
        preview: Boolean
    )

    suspend fun getReviewById(reviewId: Int)

    suspend fun addReview(
        token: String,
        contentId: Int,
        title: String,
        description: String,
        opinionId: Int,
    )

    suspend fun updateReview(
        token: String,
        title: String,
        description: String,
        reviewId: Int,
        opinion: Int,
    )

    suspend fun deleteReview(token: String, reviewId: Int)
}