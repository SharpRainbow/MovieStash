package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(login: String, password: String): String

    suspend fun logout()

    suspend fun saveCredentials(login: String, password: String, token: String)

    suspend fun saveToken(token: String)

    suspend fun register(
        login: String,
        password: String,
        nickname: String,
        email: String
    )

    fun getToken(): Flow<String>

    fun getSavedLogin(): Flow<String>

    fun getSavedPassword(): Flow<String>

}