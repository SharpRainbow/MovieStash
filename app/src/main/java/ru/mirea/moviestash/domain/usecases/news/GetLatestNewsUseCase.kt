package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.NewsRepository
import javax.inject.Inject

class GetLatestNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {

    suspend operator fun invoke(limit: Int) = repository.getNLatestNews(limit)
}