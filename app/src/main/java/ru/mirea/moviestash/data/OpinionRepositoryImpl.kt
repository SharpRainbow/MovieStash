package ru.mirea.moviestash.data

import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.mappers.toEntityList
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.OpinionRepository
import ru.mirea.moviestash.domain.entities.OpinionEntity
import javax.inject.Inject

@ApplicationScope
class OpinionRepositoryImpl @Inject constructor(
    private val movieStashApi: MovieStashApi
) : OpinionRepository {

    override suspend fun getOpinionsList(): List<OpinionEntity> {
        return movieStashApi.getOpinions().toEntityList()
    }
}