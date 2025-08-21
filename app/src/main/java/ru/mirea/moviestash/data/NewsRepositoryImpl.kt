package ru.mirea.moviestash.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.data.source.NewsPagingSource
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.NewsRepository
import ru.mirea.moviestash.domain.entities.NewsEntity
import javax.inject.Inject

@ApplicationScope
class NewsRepositoryImpl @Inject constructor(
    private val movieStashApi: MovieStashApi,
    private val externalScope: CoroutineScope
) : NewsRepository {

    private val newsFlowsCache = mutableMapOf<Int, Flow<Result<NewsEntity>>>()
    private val refreshNewsFlow = MutableSharedFlow<Int>()

    override suspend fun getNLatestNews(limit: Int): List<NewsEntity> {
        return movieStashApi.getNews(ApiProvider.FIRST_PAGE_INDEX, limit).toListEntity()
    }

    override fun getNews(): Flow<PagingData<NewsEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NewsPagingSource(
                    movieStashApi
                )
            }
        ).flow
    }

    override fun getNewsById(newsId: Int): Flow<Result<NewsEntity>> {
        return newsFlowsCache.getOrPut(newsId) {
            flow<Result<NewsEntity>> {
                val result = fetchNewsById(newsId)
                emit(result)
                refreshNewsFlow
                    .filter {
                        it == newsId
                    }.collect {
                        val result = fetchNewsById(newsId)
                        emit(result)
                    }
            }.onCompletion {
                newsFlowsCache.remove(newsId)
            }.shareIn(
                scope = externalScope,
                started = WhileSubscribed(1_000),
                replay = 1
            )
        }
    }

    private suspend fun fetchNewsById(newsId: Int): Result<NewsEntity> {
        return runCatching {
            movieStashApi.getNewsById(newsId).toEntity()
        }
    }

    override suspend fun deleteNews(token: String, newsId: Int) {
        movieStashApi.deleteNews(token, newsId)
    }

    override suspend fun addNews(
        token: String,
        title: String,
        description: String,
        imageName: String?,
        image: ByteArray?
    ) {
        val partMap = mutableMapOf(
            "title" to title.toRequestBody("text/plain".toMediaTypeOrNull()),
            "description" to description.toRequestBody("text/plain".toMediaTypeOrNull()),
        )
        val imagePart = image?.let {
            MultipartBody.Part.createFormData(
                "image",
                imageName,
                it.toRequestBody("image/*".toMediaTypeOrNull())
            )
        }
        movieStashApi.addNews(
            token,
            partMap,
            imagePart
        )
    }

    override suspend fun updateNews(
        token: String,
        newsId: Int,
        title: String,
        description: String,
        imageName: String?,
        image: ByteArray?
    ) {
        val partMap = mutableMapOf<String, RequestBody>()
        if (title.isNotBlank())
            partMap["title"] = title.toRequestBody("text/plain".toMediaTypeOrNull())
        if (description.isNotBlank())
            partMap["description"] = description.toRequestBody(
                "text/plain".toMediaTypeOrNull()
            )
        val imagePart = image?.let {
            MultipartBody.Part.createFormData(
                "image",
                imageName,
                it.toRequestBody("image/*".toMediaTypeOrNull())
            )
        }
        movieStashApi.updateNews(
            token,
            newsId,
            partMap,
            imagePart
        )
        refreshNewsFlow.emit(newsId)
    }
}