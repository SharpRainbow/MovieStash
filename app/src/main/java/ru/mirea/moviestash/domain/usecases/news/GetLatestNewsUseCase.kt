package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.NewsRepository

class GetLatestNewsUseCase(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(limit: Int) = repository.getLatestNews(limit)
}