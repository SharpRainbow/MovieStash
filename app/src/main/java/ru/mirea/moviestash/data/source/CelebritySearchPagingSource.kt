package ru.mirea.moviestash.data.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.mirea.moviestash.data.CelebrityRepositoryImpl
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toListEntityBase
import ru.mirea.moviestash.domain.CelebrityRepository
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import java.io.IOException

class CelebritySearchPagingSource(
    private val apiService: MovieStashApi,
    private val query: String
): PagingSource<Int, CelebrityEntityBase>() {

    override fun getRefreshKey(state: PagingState<Int, CelebrityEntityBase>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)

        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CelebrityEntityBase> {
        val position = params.key ?: START_PAGE_INDEX
        return try {
            val celebrityList = apiService.getCelebrities(
                page = position,
                limit = params.loadSize,
                name = query
            )
            val nextKey = if (celebrityList.isEmpty()) {
                null
            } else {
                position + (params.loadSize / CelebrityRepository.NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = celebrityList.toListEntityBase(),
                prevKey = if (position == START_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    companion object {
        private const val START_PAGE_INDEX = 1
    }

}