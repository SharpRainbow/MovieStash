package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository
import javax.inject.Inject

class SearchContentUseCase @Inject constructor(
    private val repository: ContentRepository
) {

    operator fun invoke(input: String) = repository.getContentSearchResultFlow(input)
}