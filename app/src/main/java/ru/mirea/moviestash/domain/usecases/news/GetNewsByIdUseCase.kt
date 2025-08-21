package ru.mirea.moviestash.domain.usecases.news

import ru.mirea.moviestash.domain.NewsRepository
import javax.inject.Inject

class GetNewsByIdUseCase @Inject constructor(
    private val repository: NewsRepository
) {

    operator fun invoke(newsId: Int) = repository.getNewsById(newsId)
}