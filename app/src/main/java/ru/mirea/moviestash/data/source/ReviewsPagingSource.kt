package ru.mirea.moviestash.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.domain.entities.ReviewEntity

class ReviewsPagingSource(
    private val apiService: MovieStashApi,
    private val contentId: Int
): PagingSource<Int, ReviewEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ReviewEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReviewEntity> {
        val position = params.key ?: ApiProvider.FIRST_PAGE_INDEX
        return try {
            val reviewsList = apiService.getReviewsByContentId(
                page = position,
                limit = params.loadSize,
                contentId = contentId
            )
            val nextKey = if (reviewsList.isEmpty()) {
                null
            } else {
                position + (params.loadSize / ApiProvider.NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = reviewsList.toListEntity(),
                prevKey = if (position == ApiProvider.FIRST_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}