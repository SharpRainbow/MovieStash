package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.domain.entities.NewsEntity
import java.io.File

interface NewsRepository {

    val newsListFlow: Flow<Result<List<NewsEntity>>>

    val newsFlow: Flow<Result<NewsEntity>>

    suspend fun getLatestNews(limit: Int)

    suspend fun getNews(page: Int, limit: Int)

    suspend fun getNewsById(newsId: Int)

    suspend fun deleteNews(token: String, newsId: Int)

    suspend fun addNews(
        token: String,
        title: String,
        description: String,
        imageName: String?,
        image: ByteArray?
    )

    suspend fun updateNews(
        token: String,
        newsId: Int,
        title: String,
        description: String,
        imageName: String?,
        image: ByteArray?
    )
}