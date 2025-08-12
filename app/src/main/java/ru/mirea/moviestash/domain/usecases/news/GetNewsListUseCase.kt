package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.NewsRepository

class GetNewsListUseCase(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(page: Int, limit: Int) = repository.getNews(page, limit)
}