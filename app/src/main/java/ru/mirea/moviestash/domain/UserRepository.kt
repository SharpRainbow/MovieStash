package ru.mirea.moviestash.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.domain.entities.BannedUserEntity
import ru.mirea.moviestash.domain.entities.UserEntity

interface UserRepository {

    fun getUserData(token: String): Flow<Result<UserEntity>>

    suspend fun updateUserData(
        token: String,
        nickname: String? = null,
        email: String? = null,
        password: String? = null
    )

    fun getBannedUsers(token: String): Flow<PagingData<BannedUserEntity>>

    suspend fun ban(token: String, userId: Int, reason: String)

    suspend fun unban(token: String, userId: Int)

}