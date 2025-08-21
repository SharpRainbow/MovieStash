package ru.mirea.moviestash.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import retrofit2.HttpException
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.AddUserStarDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.UserStarRepository
import ru.mirea.moviestash.domain.entities.UserStarEntity
import javax.inject.Inject

@ApplicationScope
class UserStarRepositoryImpl @Inject constructor(
    private val movieStashApi: MovieStashApi
) : UserStarRepository {

    private val refreshUserStarFlow = MutableSharedFlow<Unit>()

    override fun getUserStarByContentId(
        token: String,
        contentId: Int
    ): Flow<UserStarEntity> {
        return flow {
            try {
                val star = movieStashApi.getUserStarByContentId(token, contentId)
                emit(star.toEntity())
            } catch (e: Exception) {
                if (e !is HttpException || e.code() != 404) {
                    throw e
                }
            }
            refreshUserStarFlow
                .onEach {
                    try {
                        val star = movieStashApi.getUserStarByContentId(token, contentId)
                        emit(star.toEntity())
                    } catch (e: Exception) {
                        if (e !is HttpException || e.code() != 404) {
                            throw e
                        }
                    }
                }
                .collect()
        }
    }

    override suspend fun addUserStar(
        token: String,
        contentId: Int,
        rating: Int,
    ) {
        movieStashApi.addUserStar(
            token,
            AddUserStarDto(
                rating = rating,
                contentId = contentId,
            )
        )
        refreshUserStarFlow.emit(Unit)
    }

    override suspend fun updateUserStar(
        token: String,
        starId: Int,
        rating: Int
    ) {
        if (rating > 0) {
            movieStashApi.updateUserStar(
                token,
                starId,
                rating,
            )
        } else {
            movieStashApi.deleteUserStar(
                token,
                starId
            )
        }
        refreshUserStarFlow.emit(Unit)
    }

}