package ru.mirea.moviestash.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.CreateCollectionDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.source.CollectionByEditorPagingSource
import ru.mirea.moviestash.data.source.CollectionByUserPagingSource
import ru.mirea.moviestash.domain.CollectionRepository
import ru.mirea.moviestash.domain.entities.CollectionEntity

class CollectionRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : CollectionRepository {

    override suspend fun deleteUserCollection(token: String, collectionId: Int) {
        movieStashApi.deletePersonalCollection(token, collectionId)
    }

    override suspend fun publishCollection(token: String, collectionId: Int) {
        movieStashApi.publishCollection(token, collectionId)
    }

    override suspend fun hideCollection(token: String, collectionId: Int) {
        movieStashApi.takeOwnershipOfCollection(token, collectionId)
    }

    override suspend fun addCollection(
        token: String,
        name: String,
        description: String?,
    ) {
        movieStashApi.addPersonalCollection(
            token,
            CreateCollectionDto(
                name = name,
                description = description
            )
        )
    }

    override suspend fun updateCollection(
        token: String,
        collectionId: Int,
        name: String,
        description: String?,
    ) {
        movieStashApi.updatePersonalCollection(
            token,
            collectionId,
            CreateCollectionDto(
                name = name,
                description = description
            )
        )
    }

    override suspend fun addContentToCollection(token: String, collectionId: Int, contentId: Int) {
        movieStashApi.addContentToPersonalCollection(
            token,
            collectionId,
            contentId
        )
    }

    override suspend fun deleteContentFromCollection(
        token: String,
        collectionId: Int,
        contentId: Int
    ) {
        movieStashApi.deleteContentFromPersonalCollection(
            token,
            collectionId,
            contentId
        )
    }

    override suspend fun getPublicCollectionInfo(collectionId: Int): CollectionEntity {
        return movieStashApi.getPublicCollectionInfoById(collectionId).toEntity()
    }

    override suspend fun getUserCollectionInfo(
        token: String,
        collectionId: Int
    ): CollectionEntity {
        return movieStashApi.getPersonalCollectionById(token, collectionId).toEntity()
    }

    override fun getEditorCollectionsFlow(): Flow<PagingData<CollectionEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CollectionByEditorPagingSource(
                    movieStashApi
                )
            }
        ).flow
    }

    override fun getUserCollectionsFlow(token: String): Flow<PagingData<CollectionEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CollectionByUserPagingSource(
                    movieStashApi,
                    token
                )
            }
        ).flow
    }
}