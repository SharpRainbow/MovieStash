package ru.mirea.moviestash.domain

interface AuthRepository {

    suspend fun login(login: String, password: String)

    fun logout()

    suspend fun register(
        login: String,
        password: String,
        nickname: String,
        email: String
    )

    fun getToken(): String

    suspend fun getValidToken(): String

    fun isLoggedIn(): Boolean

    fun isModerator(): Boolean

    fun getUserId(): Int
}