package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository

class GetContentByGenreUseCase(
    private val repository: ContentRepository
) {

    operator fun invoke(
        genreId: Int
    ) = repository.getContentByGenreFlow(genreId)
}