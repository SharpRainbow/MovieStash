package ru.mirea.moviestash.data

import androidx.paging.Pager
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntityBase
import ru.mirea.moviestash.data.source.ContentByCelebrityPagingSource
import ru.mirea.moviestash.data.source.ContentByGenrePagingSource
import ru.mirea.moviestash.data.source.ContentFromPublicCollectionPagingSource
import ru.mirea.moviestash.data.source.ContentFromUserCollectionPagingSource
import ru.mirea.moviestash.data.source.ContentSearchPagingSource
import ru.mirea.moviestash.data.source.ContentTopRatedPagingSource
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.ContentRepository
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import javax.inject.Inject

@ApplicationScope
class ContentRepositoryImpl @Inject constructor(
    private val movieStashApi: MovieStashApi
) : ContentRepository {

    override suspend fun getContentById(id: Int): ContentEntity {
        return movieStashApi.getContentById(id).toEntity()
    }

    override suspend fun getMainPageContent(): List<ContentEntityBase> {
        return movieStashApi.getContents(0, 10).toListEntityBase()
    }

    override fun getTopRatedContentFlow(): Flow<PagingData<ContentEntityBase>> {
        return Pager(
            config = androidx.paging.PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ContentTopRatedPagingSource(
                    movieStashApi
                )
            }
        ).flow
    }

    override fun getContentByCelebrityFlow(
        celebrityId: Int
    ): Flow<PagingData<ContentEntityBase>> {
        return Pager(
            config = androidx.paging.PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ContentByCelebrityPagingSource(
                    movieStashApi,
                    celebrityId
                )
            }
        ).flow
    }

    override fun getContentFromPublicCollectionFlow(
        collectionId: Int
    ): Flow<PagingData<ContentEntityBase>> {
        return Pager(
            config = androidx.paging.PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ContentFromPublicCollectionPagingSource(
                    movieStashApi,
                    collectionId
                )
            }
        ).flow
    }

    override fun getContentFromUserCollectionFlow(
        token: String,
        collectionId: Int
    ): Flow<PagingData<ContentEntityBase>> {
        return Pager(
            config = androidx.paging.PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ContentFromUserCollectionPagingSource(
                    movieStashApi,
                    collectionId,
                    token
                )
            }
        ).flow
    }

    override fun getContentByGenreFlow(genreId: Int): Flow<PagingData<ContentEntityBase>> {
        return Pager(
            config = androidx.paging.PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ContentByGenrePagingSource(
                    movieStashApi,
                    genreId
                )
            }
        ).flow
    }

    override fun getContentSearchResultFlow(query: String): Flow<PagingData<ContentEntityBase>> {
        return Pager(
            config = androidx.paging.PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ContentSearchPagingSource(
                    movieStashApi,
                    query
                )
            }
        ).flow
    }
}