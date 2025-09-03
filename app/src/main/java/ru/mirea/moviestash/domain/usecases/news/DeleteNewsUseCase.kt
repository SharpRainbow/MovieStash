package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.JwtManager
import ru.mirea.moviestash.domain.NewsRepository
import javax.inject.Inject

class DeleteNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val jwtManager: JwtManager
) {

    suspend operator fun invoke(newsId: Int) = newsRepository.deleteNews(
        jwtManager.getValidToken(),
        newsId
    )
}