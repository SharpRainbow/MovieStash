package ru.mirea.moviestash.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.domain.entities.NewsEntity

interface NewsRepository {

    suspend fun getNLatestNews(limit: Int): List<NewsEntity>

    fun getNews(): Flow<PagingData<NewsEntity>>

    fun getNewsById(newsId: Int): Flow<Result<NewsEntity>>

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