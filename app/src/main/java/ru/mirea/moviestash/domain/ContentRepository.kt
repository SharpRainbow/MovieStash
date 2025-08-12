package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase

interface ContentRepository {

    val contentFlow: Flow<Result<ContentEntity>>

    val contentListFlow: Flow<Result<List<ContentEntityBase>>>

    suspend fun getContentById(id: Int)

    suspend fun searchContent(name: String, page: Int, limit: Int)

    suspend fun getMainPageContent()

    suspend fun getTopRatedContent(page: Int, limit: Int)

    suspend fun getContentByCelebrity(celebrityId: Int, page: Int, limit: Int)

    suspend fun getContentFromPublicCollection(
        collectionId: Int, page: Int, limit: Int
    )

    suspend fun getContentFromUserCollection(
        token: String, collectionId: Int, page: Int, limit: Int
    )

    suspend fun getContentByGenre(
        genreId: Int, page: Int, limit: Int
    )
}