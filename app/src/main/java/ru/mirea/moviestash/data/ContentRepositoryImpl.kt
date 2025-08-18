package ru.mirea.moviestash.data

import androidx.paging.Pager
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntityBase
import ru.mirea.moviestash.data.source.ContentSearchPagingSource
import ru.mirea.moviestash.domain.ContentRepository
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase

class ContentRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : ContentRepository {

    private val _contentFlow = MutableStateFlow<Result<ContentEntity>>(
        Result.Empty
    )
    override val contentFlow: Flow<Result<ContentEntity>>
        get() = _contentFlow.asStateFlow()
    private val _contentListFlow = MutableStateFlow<Result<List<ContentEntityBase>>>(
        Result.Empty
    )
    override val contentListFlow: Flow<Result<List<ContentEntityBase>>>
        get() = _contentListFlow.asStateFlow()

    override suspend fun getContentById(id: Int) {
        try {
            val content = movieStashApi.getContentById(id)
            _contentFlow.emit(
                Result.Success(
                    content.toEntity()
                )
            )
        } catch (e: Exception) {
            _contentFlow.emit(Result.Error(e))
        }
    }

    override suspend fun searchContent(name: String, page: Int, limit: Int) {
        try {
            val contentList = movieStashApi.getContents(
                page, limit, name
            )
            _contentListFlow.emit(Result.Success(contentList.toListEntityBase()))
        } catch (e: Exception) {
            _contentListFlow.emit(Result.Error(e))
        }
    }

    override suspend fun getMainPageContent() {
        try {
            val contentList = movieStashApi.getContents(0, 10)
            _contentListFlow.emit(Result.Success(contentList.toListEntityBase()))
        } catch (e: Exception) {
            _contentListFlow.emit(Result.Error(e))
        }
    }

    override suspend fun getTopRatedContent(page: Int, limit: Int) {
        try {
            val contentList = movieStashApi.getBestContents(page, limit)
            _contentListFlow.emit(Result.Success(contentList.toListEntityBase()))
        } catch (e: Exception) {
            _contentListFlow.emit(Result.Error(e))
        }
    }

    override suspend fun getContentByCelebrity(
        celebrityId: Int,
        page: Int,
        limit: Int
    ) {
        try {
            val contentList = movieStashApi.getContentsByCelebrityId(
                celebrityId
            )
            _contentListFlow.emit(Result.Success(contentList.toListEntityBase()))
        } catch (e: Exception) {
            _contentListFlow.emit(Result.Error(e))
        }
    }

    override suspend fun getContentFromPublicCollection(
        collectionId: Int,
        page: Int,
        limit: Int
    ) {
        try {
            val contentList = movieStashApi.getContentsFromPublicCollection(
                collectionId, page, limit
            )
            _contentListFlow.emit(Result.Success(contentList.toListEntityBase()))
        } catch (e: Exception) {
            _contentListFlow.emit(Result.Error(e))
        }
    }

    override suspend fun getContentFromUserCollection(
        token: String,
        collectionId: Int,
        page: Int,
        limit: Int
    ) {
        try {
            val contentList = movieStashApi.getContentsFromUserCollection(
                token, collectionId, page, limit
            )
            _contentListFlow.emit(Result.Success(contentList.toListEntityBase()))
        } catch (e: Exception) {
            _contentListFlow.emit(Result.Error(e))
        }
    }

    override suspend fun getContentByGenre(
        genreId: Int,
        page: Int,
        limit: Int
    ) {
        try {
            val contentList = movieStashApi.getContents(
                page, limit, genre = genreId
            )
            _contentListFlow.emit(Result.Success(contentList.toListEntityBase()))
        } catch (e: Exception) {
            _contentListFlow.emit(Result.Error(e))
        }
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