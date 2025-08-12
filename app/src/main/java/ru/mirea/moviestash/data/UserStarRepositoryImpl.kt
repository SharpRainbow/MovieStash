package ru.mirea.moviestash.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.AddUserStarDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.domain.UserStarRepository
import ru.mirea.moviestash.domain.entities.UserStarEntity

class UserStarRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : UserStarRepository {

    private val _userStarFlow = MutableStateFlow<Result<UserStarEntity>>(
        Result.Empty
    )
    override val userStarFlow: Flow<Result<UserStarEntity>>
        get() = _userStarFlow.asStateFlow()

    override suspend fun getUserStarByContentId(token: String, contentId: Int) {
        try {
            val userStars = movieStashApi.getUserStarByContentId(token, contentId)
            _userStarFlow.emit(Result.Success(userStars.toEntity()))
        } catch (e: Exception) {
            if (e.message?.contains(NOT_FOUND) == false) {
                _userStarFlow.emit(Result.Error(e))
            }
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
    }

    companion object {

        private const val NOT_FOUND = "HTTP 404 Not Found"
    }
}