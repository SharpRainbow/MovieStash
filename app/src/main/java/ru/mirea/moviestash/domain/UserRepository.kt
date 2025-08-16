package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.domain.entities.BannedUserEntity
import ru.mirea.moviestash.domain.entities.UserEntity

interface UserRepository {

    val userListFlow: Flow<Result<List<BannedUserEntity>>>

    // TODO: Add a flow to get the current user data

    suspend fun getUserData(token: String): UserEntity

    suspend fun updateUserData(
        token: String,
        nickname: String? = null,
        email: String? = null,
        password: String? = null
    )

    suspend fun getBannedUsers(token: String, page: Int, limit: Int)

    suspend fun ban(token: String, userId: Int, reason: String)

    suspend fun unban(token: String, userId: Int)

}