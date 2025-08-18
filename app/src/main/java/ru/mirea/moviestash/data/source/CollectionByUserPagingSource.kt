package ru.mirea.moviestash.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.domain.entities.CollectionEntity

class CollectionByUserPagingSource(
    private val apiService: MovieStashApi,
    private val token: String
): PagingSource<Int, CollectionEntity>() {

    override fun getRefreshKey(state: PagingState<Int, CollectionEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CollectionEntity> {
        val position = params.key ?: ApiProvider.FIRST_PAGE_INDEX
        return try {
            val contentList = apiService.getPersonalCollections(
                token,
                position,
                params.loadSize
            )
            val nextKey =
                if (contentList.isEmpty()) {
                    null
                } else {
                    position + (params.loadSize / ApiProvider.NETWORK_PAGE_SIZE)
                }
            LoadResult.Page(
                data = contentList.toListEntity(),
                prevKey = if (position == ApiProvider.FIRST_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}