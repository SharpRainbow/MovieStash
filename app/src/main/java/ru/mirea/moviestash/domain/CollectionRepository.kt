package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.domain.entities.CollectionEntity

interface CollectionRepository {

    val collectionFlow: Flow<Result<CollectionEntity>>

    val collectionsListFlow: Flow<Result<List<CollectionEntity>>>

    suspend fun getPublicCollectionInfo(collectionId: Int)

    suspend fun getUserCollectionInfo(token: String, collectionId: Int)

    suspend fun getEditorCollections(
        page: Int,
        limit: Int
    )

    suspend fun getUserCollections(
        token: String,
        page: Int,
        limit: Int
    )

    suspend fun deleteUserCollection(
        token: String,
        collectionId: Int
    )

    suspend fun publishCollection(
        token: String,
        collectionId: Int
    )

    suspend fun hideCollection(
        token: String,
        collectionId: Int
    )

    suspend fun addCollection(
        token: String,
        name: String,
        description: String?,
    )

    suspend fun updateCollection(
        token: String,
        collectionId: Int,
        name: String,
        description: String?,
    )

    suspend fun addContentToCollection(
        token: String,
        collectionId: Int,
        contentId: Int
    )

    suspend fun deleteContentFromCollection(
        token: String,
        collectionId: Int,
        contentId: Int
    )
}