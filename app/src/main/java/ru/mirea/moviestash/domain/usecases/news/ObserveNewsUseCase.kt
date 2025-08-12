package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.NewsRepository

class ObserveNewsUseCase(
    private val repository: NewsRepository
) {

    operator fun invoke() = repository.newsFlow
}