package ru.mirea.moviestash.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.CelebrityEntity

interface CelebrityRepository {

    val celebrityListFlow: Flow<Result<List<CelebrityEntityBase>>>

    val castListFlow: Flow<Result<List<CelebrityInContentDto>>>

    val crewListFlow: Flow<Result<List<CelebrityInContentDto>>>

    val celebrityFlow: Flow<Result<CelebrityEntity>>

    suspend fun searchForCelebrity(
        celebrityName: String,
        page: Int,
        limit: Int
    )

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

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}