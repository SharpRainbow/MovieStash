package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.NewsRepository

class ObserveNewsListUseCase(
    private val repository: NewsRepository
) {

    operator fun invoke() = repository.newsListFlow
}