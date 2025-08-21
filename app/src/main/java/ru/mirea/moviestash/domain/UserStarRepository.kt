package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.domain.entities.UserStarEntity

interface UserStarRepository {

    fun getUserStarByContentId(
        token: String,
        contentId: Int,
    ): Flow<UserStarEntity>

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