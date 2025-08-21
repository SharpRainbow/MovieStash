package ru.mirea.moviestash.domain.usecases.content

import ru.mirea.moviestash.domain.ContentRepository
import javax.inject.Inject

class GetContentByGenreUseCase @Inject constructor(
    private val repository: ContentRepository
) {

    operator fun invoke(
        genreId: Int
    ) = repository.getContentByGenreFlow(genreId)
}