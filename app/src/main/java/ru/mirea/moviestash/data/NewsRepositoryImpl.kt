package ru.mirea.moviestash.data

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.domain.NewsRepository
import ru.mirea.moviestash.domain.entities.NewsEntity

class NewsRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : NewsRepository {

    private val _newsListFlow = MutableSharedFlow<Result<List<NewsEntity>>>(
        1, 0, BufferOverflow.DROP_OLDEST
    )
    override val newsListFlow: Flow<Result<List<NewsEntity>>>
        get() = _newsListFlow.asSharedFlow()
    private val _newsFlow = MutableSharedFlow<Result<NewsEntity>>(
        1, 0, BufferOverflow.DROP_OLDEST
    )
    override val newsFlow: Flow<Result<NewsEntity>>
        get() = _newsFlow.asSharedFlow()

    override suspend fun getLatestNews(limit: Int) {
        try {
            val lastNews = movieStashApi.getNews(1, limit)
            _newsListFlow.emit(Result.Success(
                lastNews.toListEntity()
            ))
        } catch (e: Exception) {
            _newsListFlow.emit(
                Result.Error(
                    e
                )
            )
        }
    }

    override suspend fun getNews(page: Int, limit: Int) {
        try {
            val newsList = movieStashApi.getNews(page, limit)
            _newsListFlow.emit(Result.Success(
                newsList.toListEntity()
            ))
        } catch (e: Exception) {
            _newsListFlow.emit(
                Result.Error(
                    e
                )
            )
        }
    }

    override suspend fun getNewsById(newsId: Int) {
        try {
            val news = movieStashApi.getNewsById(newsId)
            _newsFlow.emit(Result.Success(
                news.toEntity()
            ))
        } catch (e: Exception) {
            _newsFlow.emit(
                Result.Error(
                    e
                )
            )
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
        getNewsById(newsId)
    }
}