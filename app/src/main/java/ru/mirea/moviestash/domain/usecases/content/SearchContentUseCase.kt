package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class SearchContentUseCase(
    private val repository: ContentRepository
) {

    operator fun invoke(input: String) = repository.getContentSearchResultFlow(input)
}