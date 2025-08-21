package ru.mirea.moviestash.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.domain.entities.ReviewEntity

interface ReviewRepository {

    suspend fun getFirstNReviewsByContentId(
        contentId: Int,
        limit: Int,
        token: String?
    ): List<ReviewEntity>

    fun getReviewsByContentId(
        contentId: Int,
    ): Flow<PagingData<ReviewEntity>>

    fun getReviewById(reviewId: Int): Flow<Result<ReviewEntity>>

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