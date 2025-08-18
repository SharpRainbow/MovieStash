package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class GetContentUseCase(
    private val repository: ContentRepository
) {

    suspend operator fun invoke(contentId: Int) = repository.getContentById(contentId)
}