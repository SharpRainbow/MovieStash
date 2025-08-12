package ru.mirea.moviestash.data

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.CreateCollectionDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.domain.CollectionRepository
import ru.mirea.moviestash.domain.entities.CollectionEntity

class CollectionRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : CollectionRepository {

    private val _collectionsListFlow = MutableSharedFlow<Result<List<CollectionEntity>>>(
        1, 0, BufferOverflow.DROP_OLDEST
    )
    override val collectionsListFlow: Flow<Result<List<CollectionEntity>>>
        get() = _collectionsListFlow.asSharedFlow()
    private val _collectionFlow = MutableSharedFlow<Result<CollectionEntity>>(
        1, 0, BufferOverflow.DROP_OLDEST
    )
    override val collectionFlow: Flow<Result<CollectionEntity>>
        get() = _collectionFlow.asSharedFlow()

    override suspend fun getEditorCollections(page: Int, limit: Int) {
        try {
            val collections = movieStashApi.getPublicCollections(page, limit)
            _collectionsListFlow.emit(Result.Success(collections.toListEntity()))
        } catch (e: Exception) {
            _collectionsListFlow.emit(Result.Error(e))
        }
    }

    override suspend fun getUserCollections(
        token: String,
        page: Int,
        limit: Int
    ) {
        try {
            val collections = movieStashApi.getPersonalCollections(
                token,
                page,
                limit
            )
            _collectionsListFlow.emit(Result.Success(collections.toListEntity()))
        } catch (e: Exception) {
            _collectionsListFlow.emit(Result.Error(e))
        }
    }

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

    override suspend fun getPublicCollectionInfo(collectionId: Int) {
        try {
            _collectionFlow.emit(
                Result.Success(
                    movieStashApi.getPublicCollectionInfoById(
                        collectionId
                    ).toEntity()
                )
            )
        } catch (e: Exception) {
            _collectionFlow.emit(
                Result.Error(e)
            )
        }
    }

    override suspend fun getUserCollectionInfo(token: String, collectionId: Int) {
        try {
            _collectionFlow.emit(
                Result.Success(
                    movieStashApi.getPersonalCollectionById(
                        token,
                        collectionId
                    ).toEntity()
                )
            )
        } catch (e: Exception) {
            _collectionFlow.emit(
                Result.Error(e)
            )
        }
    }
}