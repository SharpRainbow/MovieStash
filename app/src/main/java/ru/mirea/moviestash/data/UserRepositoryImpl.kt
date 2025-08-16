package ru.mirea.moviestash.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.BanRequestDto
import ru.mirea.moviestash.data.api.dto.UpdateUserDto
import ru.mirea.moviestash.data.api.dto.UserDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toEntityList
import ru.mirea.moviestash.domain.UserRepository
import ru.mirea.moviestash.domain.entities.BannedUserEntity
import ru.mirea.moviestash.domain.entities.UserEntity

class UserRepositoryImpl(
    private val movieStashApi: MovieStashApi
) : UserRepository {

    private val _userListFlow = MutableStateFlow<Result<List<BannedUserEntity>>>(
        Result.Empty
    )
    override val userListFlow: Flow<Result<List<BannedUserEntity>>>
        get() = _userListFlow.asStateFlow()

    override suspend fun getUserData(token: String): UserEntity {
        return movieStashApi.getCurrentUserData(token).toEntity()
    }

    override suspend fun updateUserData(
        token: String,
        nickname: String?,
        email: String?,
        password: String?
    ) {
        movieStashApi.updateUserData(
            token,
            UpdateUserDto(
                nickname = nickname,
                email = email,
                password = password
            )
        )
    }

    override suspend fun getBannedUsers(token: String, page: Int, limit: Int) {
        try {
            _userListFlow.emit(
                Result.Success(
                    movieStashApi.getBannedUsers(token, page, limit).toEntityList()
                )
            )
        } catch (e: Exception) {
            _userListFlow.emit(
                Result.Error(e)
            )
            return
        }
    }

    override suspend fun ban(token: String, userId: Int, reason: String) {
        movieStashApi.banUser(token, userId, BanRequestDto(reason))
    }

    override suspend fun unban(token: String, userId: Int) {
        movieStashApi.unbanUser(token, userId)
    }

}