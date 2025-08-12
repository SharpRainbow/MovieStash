package ru.mirea.moviestash.data

import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntityList
import ru.mirea.moviestash.domain.OpinionRepository
import ru.mirea.moviestash.domain.entities.OpinionEntity

class OpinionRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : OpinionRepository {

    override suspend fun getOpinionsList(): List<OpinionEntity> {
        return movieStashApi.getOpinions().toEntityList()
    }
}