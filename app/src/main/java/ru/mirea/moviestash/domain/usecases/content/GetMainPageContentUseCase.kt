package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class GetMainPageContentUseCase(
    private val repository: ContentRepository
) {

    suspend operator fun invoke() = repository.getMainPageContent()
}