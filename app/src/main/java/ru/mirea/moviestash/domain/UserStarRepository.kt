package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.domain.entities.UserStarEntity

interface UserStarRepository {

    val userStarFlow: Flow<Result<UserStarEntity>>

    suspend fun getUserStarByContentId(
        token: String,
        contentId: Int,
    )

    suspend fun addUserStar(
        token: String,
        contentId: Int,
        rating: Int,
    )

    suspend fun updateUserStar(
        token: String,
        starId: Int,
        rating: Int
    )
}