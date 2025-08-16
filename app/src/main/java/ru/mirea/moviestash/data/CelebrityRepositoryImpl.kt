package ru.mirea.moviestash.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toListEntityBase
import ru.mirea.moviestash.domain.CelebrityRepository
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase

class CelebrityRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : CelebrityRepository {

    private val _celebrityListFlow = MutableStateFlow<Result<List<CelebrityEntityBase>>>(
        Result.Empty
    )
    override val celebrityListFlow: Flow<Result<List<CelebrityEntityBase>>>
        get() = _celebrityListFlow.asStateFlow()
    private val _castListFlow = MutableStateFlow<Result<List<CelebrityInContentDto>>>(
        Result.Success(
            emptyList()
        )
    )
    override val castListFlow: Flow<Result<List<CelebrityInContentDto>>>
        get() = _castListFlow.asStateFlow()
    private val _crewListFlow = MutableStateFlow<Result<List<CelebrityInContentDto>>>(
        Result.Success(emptyList())
    )
    override val crewListFlow: Flow<Result<List<CelebrityInContentDto>>>
        get() = _crewListFlow.asStateFlow()
    private val _celebrityFlow = MutableStateFlow<Result<CelebrityEntity>>(
        Result.Empty
    )
    override val celebrityFlow: Flow<Result<CelebrityEntity>>
        get() = _celebrityFlow.asStateFlow()

    override suspend fun searchForCelebrity(celebrityName: String, page: Int, limit: Int) {
        try {
            _celebrityListFlow.emit(
                Result.Success(
                    movieStashApi
                        .getCelebrities(page, limit, celebrityName)
                        .toListEntityBase()
                )
            )
        } catch (e: Exception) {
            _celebrityListFlow.emit(Result.Error(e))
            return
        }
    }

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
                        )
                    )
                )
            } else {
                _crewListFlow.emit(
                    Result.Success(
                        movieStashApi.getCrewByContentId(
                            contentId,
                            page,
                            limit
                        )
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
}