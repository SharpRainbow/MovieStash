package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository
import javax.inject.Inject

class GetMainPageContentUseCase @Inject constructor(
    private val repository: ContentRepository
) {

    suspend operator fun invoke() = repository.getMainPageContent()
}