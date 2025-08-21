package ru.mirea.moviestash.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import ru.mirea.moviestash.data.api.ApiProvider
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.BanRequestDto
import ru.mirea.moviestash.data.api.dto.UpdateUserDto
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.source.BannedUserPagingSource
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.UserRepository
import ru.mirea.moviestash.domain.entities.BannedUserEntity
import ru.mirea.moviestash.domain.entities.UserEntity
import javax.inject.Inject

@ApplicationScope
class UserRepositoryImpl @Inject constructor(
    private val movieStashApi: MovieStashApi,
    private val externalScope: CoroutineScope
) : UserRepository {

    @Volatile
    private var cachedUserData: Flow<Result<UserEntity>>? = null
    private val updateUserDataFlow = MutableSharedFlow<Unit>()

    override fun getUserData(token: String): Flow<Result<UserEntity>> {
        return cachedUserData ?: flow<Result<UserEntity>> {
            emit(fetchUserData(token))
            updateUserDataFlow
                .onEach {
                    emit(fetchUserData(token))
                }
                .collect()
        }.onCompletion {
            cachedUserData = null
        }.shareIn(
            scope = externalScope,
            started = SharingStarted.WhileSubscribed(1_000),
            replay = 1
        ).also {
            cachedUserData = it
        }
    }

    private suspend fun fetchUserData(token: String): Result<UserEntity> {
        return try {
            val userDto = movieStashApi.getCurrentUserData(token)
            Result.success(userDto.toEntity())
        } catch (e: Exception) {
            Result.failure(e)
        }
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
        updateUserDataFlow.emit(Unit)
    }

    override fun getBannedUsers(token: String): Flow<PagingData<BannedUserEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiProvider.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                BannedUserPagingSource(
                    apiService = movieStashApi,
                    token = token
                )
            }
        ).flow
    }

    override suspend fun ban(token: String, userId: Int, reason: String) {
        movieStashApi.banUser(token, userId, BanRequestDto(reason))
    }

    override suspend fun unban(token: String, userId: Int) {
        movieStashApi.unbanUser(token, userId)
    }

}