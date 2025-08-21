package ru.mirea.moviestash.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity

interface CelebrityRepository {

    suspend fun getFirstNCelebrityByContentId(
        contentId: Int,
        limit: Int,
        actors: Boolean = true
    ): List<CelebrityInContentEntity>

    suspend fun getCelebrityById(celebrityId: Int): CelebrityEntity

    fun getCelebritySearchResultFlow(
        query: String
    ): Flow<PagingData<CelebrityEntityBase>>

    fun getCelebrityByContentIdFlow(
        contentId: Int,
        actors: Boolean
    ): Flow<PagingData<CelebrityInContentEntity>>

}