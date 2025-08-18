package ru.mirea.moviestash.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity

interface CelebrityRepository {

    val celebrityListFlow: Flow<Result<List<CelebrityEntityBase>>>

    val castListFlow: Flow<Result<List<CelebrityInContentEntity>>>

    val crewListFlow: Flow<Result<List<CelebrityInContentEntity>>>

    val celebrityFlow: Flow<Result<CelebrityEntity>>

    suspend fun getCelebrityByContentId(
        contentId: Int,
        page: Int,
        limit: Int,
        actors: Boolean = true
    )

    suspend fun getCelebrityById(celebrityId: Int)

    fun getCelebritySearchResultFlow(
        query: String
    ): Flow<PagingData<CelebrityEntityBase>>

    fun getCelebrityByContentIdFlow(
        contentId: Int,
        actors: Boolean
    ): Flow<PagingData<CelebrityInContentEntity>>

}