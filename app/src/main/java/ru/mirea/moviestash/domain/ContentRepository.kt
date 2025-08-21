package ru.mirea.moviestash.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase

interface ContentRepository {

    suspend fun getContentById(id: Int): ContentEntity

    suspend fun getMainPageContent(): List<ContentEntityBase>

    fun getTopRatedContentFlow(): Flow<PagingData<ContentEntityBase>>

    fun getContentByCelebrityFlow(celebrityId: Int): Flow<PagingData<ContentEntityBase>>

    fun getContentFromPublicCollectionFlow(
        collectionId: Int
    ): Flow<PagingData<ContentEntityBase>>

    fun getContentFromUserCollectionFlow(
        token: String,
        collectionId: Int
    ): Flow<PagingData<ContentEntityBase>>

    fun getContentByGenreFlow(
        genreId: Int
    ): Flow<PagingData<ContentEntityBase>>

    fun getContentSearchResultFlow(
        query: String
    ): Flow<PagingData<ContentEntityBase>>

}