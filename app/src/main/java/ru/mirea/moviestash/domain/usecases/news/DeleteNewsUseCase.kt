package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.NewsRepository

class DeleteNewsUseCase(
    private val newsRepository: NewsRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(newsId: Int) = newsRepository.deleteNews(
        authRepository.getValidToken(),
        newsId
    )
}