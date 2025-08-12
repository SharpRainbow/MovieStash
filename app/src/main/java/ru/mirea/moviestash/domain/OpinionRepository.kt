package ru.mirea.moviestash.domain

import ru.mirea.moviestash.domain.entities.OpinionEntity

interface OpinionRepository {

    suspend fun getOpinionsList(): List<OpinionEntity>
}