package ru.mirea.moviestash.domain.usecases.opinion

import ru.mirea.moviestash.domain.OpinionRepository

class GetOpinionsListUseCase(
    private val repository: OpinionRepository
) {

    suspend operator fun invoke() = repository.getOpinionsList()
}