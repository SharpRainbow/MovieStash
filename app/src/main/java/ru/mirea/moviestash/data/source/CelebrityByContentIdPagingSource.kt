package ru.mirea.moviestash.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity

class CelebrityByContentIdPagingSource(
    private val apiService: MovieStashApi,
    private val contentId: Int,
    private val actors: Boolean
): PagingSource<Int, CelebrityInContentEntity>() {

    override fun getRefreshKey(state: PagingState<Int, CelebrityInContentEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CelebrityInContentEntity> {
        val position = params.key ?: ApiProvider.FIRST_PAGE_INDEX
        return try {
            val celebrityList =
                if (actors) {
                    apiService.getCastByContentId(
                        page = position,
                        limit = params.loadSize,
                        contentId = contentId
                    )
                } else {
                    apiService.getCrewByContentId(
                        page = position,
                        limit = params.loadSize,
                        contentId = contentId
                    )
                }
            val nextKey = if (celebrityList.isEmpty()) {
                null
            } else {
                position + (params.loadSize / ApiProvider.NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = celebrityList.toListEntity(),
                prevKey = if (position == ApiProvider.FIRST_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}