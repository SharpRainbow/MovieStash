package ru.mirea.moviestash.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.domain.entities.CollectionEntity

interface CollectionRepository {

    suspend fun getPublicCollectionInfo(collectionId: Int): CollectionEntity

    suspend fun getUserCollectionInfo(token: String, collectionId: Int): CollectionEntity

    fun getEditorCollectionsFlow(): Flow<PagingData<CollectionEntity>>

    fun getUserCollectionsFlow(token: String): Flow<PagingData<CollectionEntity>>

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