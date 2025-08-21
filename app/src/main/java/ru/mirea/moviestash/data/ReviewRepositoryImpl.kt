package ru.mirea.moviestash.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.AddReviewDto
import ru.mirea.moviestash.data.api.dto.UpdateReviewDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.data.source.ReviewsPagingSource
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.ReviewRepository
import ru.mirea.moviestash.domain.entities.ReviewEntity
import javax.inject.Inject

@ApplicationScope
class ReviewRepositoryImpl @Inject constructor(
    private val movieStashApi: MovieStashApi,
    private val externalScope: CoroutineScope
) : ReviewRepository {

    private val refreshReviewFlow = MutableSharedFlow<Int>()
    private val reviewCache = mutableMapOf<Int, Flow<Result<ReviewEntity>>>()

    override suspend fun getFirstNReviewsByContentId(
        contentId: Int,
        limit: Int,
        token: String?
    ): List<ReviewEntity> {
        return movieStashApi.getReviewsByContentId(
            contentId,
            ApiProvider.FIRST_PAGE_INDEX,
            limit,
            token
        ).toListEntity()
    }

    override fun getReviewsByContentId(
        contentId: Int
    ): Flow<PagingData<ReviewEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ReviewsPagingSource(
                    apiService = movieStashApi,
                    contentId = contentId
                )
            }
        ).flow
    }

    override fun getReviewById(reviewId: Int): Flow<Result<ReviewEntity>> {
        return reviewCache.getOrPut(reviewId) {
            flow<Result<ReviewEntity>> {
                emit(fetchReviewById(reviewId))
                refreshReviewFlow
                    .filter {
                        it == reviewId
                    }
                    .onEach {
                        emit(fetchReviewById(reviewId))
                    }.collect()
            }.onCompletion {
                reviewCache.remove(reviewId)
            }.shareIn(
                scope = externalScope,
                started = SharingStarted.WhileSubscribed(1_000),
                replay = 1
            )
        }
    }

    private suspend fun fetchReviewById(reviewId: Int): Result<ReviewEntity> {
        return runCatching {
            movieStashApi.getReviewById(reviewId).toEntity()
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
        refreshReviewFlow.emit(reviewId)
    }

    override suspend fun deleteReview(token: String, reviewId: Int) {
        movieStashApi.deleteReview(token, reviewId)
    }
}