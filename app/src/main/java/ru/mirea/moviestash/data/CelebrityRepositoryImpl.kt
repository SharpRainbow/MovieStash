package ru.mirea.moviestash.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntity
import ru.mirea.moviestash.data.mappers.toListEntityBase
import ru.mirea.moviestash.data.source.CelebrityByContentIdPagingSource
import ru.mirea.moviestash.data.source.CelebritySearchPagingSource
import ru.mirea.moviestash.domain.CelebrityRepository
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity

class CelebrityRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : CelebrityRepository {

    private val _celebrityListFlow = MutableStateFlow<Result<List<CelebrityEntityBase>>>(
        Result.Empty
    )
    override val celebrityListFlow: Flow<Result<List<CelebrityEntityBase>>>
        get() = _celebrityListFlow.asStateFlow()
    private val _castListFlow = MutableStateFlow<Result<List<CelebrityInContentEntity>>>(
        Result.Success(
            emptyList()
        )
    )
    override val castListFlow: Flow<Result<List<CelebrityInContentEntity>>>
        get() = _castListFlow.asStateFlow()
    private val _crewListFlow = MutableStateFlow<Result<List<CelebrityInContentEntity>>>(
        Result.Success(emptyList())
    )
    override val crewListFlow: Flow<Result<List<CelebrityInContentEntity>>>
        get() = _crewListFlow.asStateFlow()
    private val _celebrityFlow = MutableStateFlow<Result<CelebrityEntity>>(
        Result.Empty
    )
    override val celebrityFlow: Flow<Result<CelebrityEntity>>
        get() = _celebrityFlow.asStateFlow()

    override suspend fun getCelebrityByContentId(
        contentId: Int,
        page: Int,
        limit: Int,
        actors: Boolean
    ) {
        try {
            if (actors) {
                _castListFlow.emit(
                    Result.Success(
                        movieStashApi.getCastByContentId(
                            contentId,
                            page,
                            limit
                        ).toListEntity()
                    )
                )
            } else {
                _crewListFlow.emit(
                    Result.Success(
                        movieStashApi.getCrewByContentId(
                            contentId,
                            page,
                            limit
                        ).toListEntity()
                    )
                )
            }
        } catch (e: Exception) {
            if (actors) {
                _castListFlow.emit(Result.Error(e))
            } else {
                _crewListFlow.emit(Result.Error(e))
            }
            return
        }
    }

    override suspend fun getCelebrityById(celebrityId: Int) {
        try {
            val celebrity = movieStashApi.getCelebrityById(celebrityId)
            _celebrityFlow.emit(
                Result.Success(celebrity.toEntity())
            )
        } catch (e: Exception) {
            _celebrityFlow.emit(Result.Error(e))
            return
        }
    }

    override fun getCelebritySearchResultFlow(query: String): Flow<PagingData<CelebrityEntityBase>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CelebritySearchPagingSource(
                    movieStashApi,
                    query
                )
            }
        ).flow
    }

    override fun getCelebrityByContentIdFlow(
        contentId: Int,
        actors: Boolean
    ): Flow<PagingData<CelebrityInContentEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CelebrityByContentIdPagingSource(
                    movieStashApi,
                    contentId,
                    actors
                )
            }
        ).flow
    }
}