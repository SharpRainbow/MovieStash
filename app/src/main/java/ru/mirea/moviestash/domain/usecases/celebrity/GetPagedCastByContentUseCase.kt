package ru.mirea.moviestash.domain.usecases.celebrity

import ru.mirea.moviestash.domain.CelebrityRepository
import javax.inject.Inject

class GetPagedCastByContentUseCase @Inject constructor(
    private val repository: CelebrityRepository
) {

    operator fun invoke(contentId: Int)
        = repository.getCelebrityByContentIdFlow(contentId, true)
}