package ru.mirea.moviestash.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.data.source.CelebrityByContentIdPagingSource
import ru.mirea.moviestash.data.source.CelebritySearchPagingSource
import ru.mirea.moviestash.domain.CelebrityRepository
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity

class CelebrityRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : CelebrityRepository {

    override suspend fun getFirstNCelebrityByContentId(
        contentId: Int,
        limit: Int,
        actors: Boolean
    ): List<CelebrityInContentEntity> {
        return if (actors) {
            movieStashApi.getCastByContentId(
                contentId,
                ApiProvider.FIRST_PAGE_INDEX,
                limit
            ).toListEntity()
        } else {
            movieStashApi.getCrewByContentId(
                contentId,
                ApiProvider.FIRST_PAGE_INDEX,
                limit
            ).toListEntity()
        }
    }

    override suspend fun getCelebrityById(celebrityId: Int): CelebrityEntity {
        return movieStashApi.getCelebrityById(celebrityId).toEntity()
    }

    override fun getCelebritySearchResultFlow(query: String): Flow<PagingData<CelebrityEntityBase>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CelebritySearchPagingSource(
                    movieStashApi,
                    query
                )
            }
        ).flow
    }

    override fun getCelebrityByContentIdFlow(
        contentId: Int,
        actors: Boolean
    ): Flow<PagingData<CelebrityInContentEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CelebrityByContentIdPagingSource(
                    movieStashApi,
                    contentId,
                    actors
                )
            }
        ).flow
    }
}