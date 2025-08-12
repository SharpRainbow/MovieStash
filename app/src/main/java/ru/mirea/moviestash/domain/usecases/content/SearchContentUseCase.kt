package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class SearchContentUseCase(
    private val repository: ContentRepository
) {

    suspend operator fun invoke(
        name: String,
        page: Int,
        limit: Int
    ) = repository.searchContent(
        name, page, limit
    )
}