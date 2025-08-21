package ru.mirea.moviestash.domain.usecases.opinion

import ru.mirea.moviestash.domain.OpinionRepository
import javax.inject.Inject

class GetOpinionsListUseCase @Inject constructor(
    private val repository: OpinionRepository
) {

    suspend operator fun invoke() = repository.getOpinionsList()
}