package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class GetContentByGenreUseCase(
    private val repository: ContentRepository
) {

    suspend operator fun invoke(
        genreId: Int,
        page: Int,
        limit: Int
    ) = repository.getContentByGenre(genreId, page, limit)
}