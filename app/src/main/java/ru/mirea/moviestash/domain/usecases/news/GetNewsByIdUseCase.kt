package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.NewsRepository

class GetNewsByIdUseCase(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(newsId: Int) = repository.getNewsById(newsId)
}