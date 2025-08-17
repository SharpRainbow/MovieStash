package ru.mirea.moviestash.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.core.content.edit
import org.json.JSONObject
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.CredentialsDto
import ru.mirea.moviestash.data.api.dto.RegisterDto
import ru.mirea.moviestash.domain.AuthRepository
import ru.mirea.moviestash.domain.entities.Role
import ru.mirea.moviestash.domain.entities.UserData
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AuthRepositoryImpl(
    private val context: Context,
    private val movieStashApi: MovieStashApi
) : AuthRepository {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
    }

    override suspend fun register(
        login: String,
        password: String,
        nickname: String,
        email: String
    ) {
        movieStashApi.register(
            RegisterDto(
                login = login,
                nickname = nickname,
                email = email,
                password = password
            )
        )
    }

    override suspend fun login(login: String, password: String) {
        val token = movieStashApi.login(
            CredentialsDto(
                login = login,
                password = password
            )
        )
        cachedUser = parseJwtToken(token.token)
        sharedPreferences.edit {
            putString(LOGIN_KEY, login)
            putString(PASSWORD_KEY, password)
            putString(TOKEN_KEY, token.token)
        }
    }

    override fun logout() {
        cachedUser = null
        sharedPreferences.edit {
            clear()
        }
    }

    override fun isLoggedIn(): Boolean {
        return !getToken().isEmpty()
    }

    override fun getToken(): String {
        return sharedPreferences.getString(TOKEN_KEY, "") ?: ""
    }

    override suspend fun getValidToken(): String {
        val token = getToken()
        if (isTokenValid(token)) {
            return token
        } else {
            val login = getLogin()
            val password = getPassword()
            if (!login.isNullOrBlank() && !password.isNullOrBlank()) {
                login(login, password)
            }
            return getToken()
        }
    }

    private fun getLogin(): String? {
        return sharedPreferences.getString(LOGIN_KEY, "")
    }

    private fun getPassword(): String? {
        return sharedPreferences.getString(PASSWORD_KEY, "")
    }

    override fun isModerator(): Boolean {
        if (cachedUser != null) {
            return cachedUser?.role == Role.MODERATOR
        } else {
            val token = getToken().ifEmpty {
                return false
            }
            cachedUser = parseJwtToken(token)
            return cachedUser?.role == Role.MODERATOR
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun parseJwtToken(token: String): UserData {
        val payload = JSONObject(String(
            Base64.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL).decode(token.split('.')[1])
        ))
        return UserData(
            userId = payload.getString("sub").toInt(),
            role = payload.getJSONArray("aud").getString(0).let {
                when (it) {
                    "moderator" -> Role.MODERATOR
                    else -> Role.USER
                }
            }
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun isTokenValid(token: String): Boolean {
        return try {
            val payload = JSONObject(String(
                Base64.withPadding(
                    Base64.PaddingOption.PRESENT_OPTIONAL
                ).decode(token.split('.')[1])
            ))
            payload.getLong("exp") > System.currentTimeMillis() / 1000
        } catch (e: Exception) {
            false
        }
    }

    override fun getUserId(): Int {
        if (cachedUser != null) {
            return cachedUser?.userId ?: 0
        } else {
            val token = getToken().ifEmpty {
                return 0
            }
            cachedUser = parseJwtToken(token)
            return cachedUser?.userId ?: 0
        }
    }

    companion object {
        private const val PREFERENCE_NAME = "AUTH"
        private const val LOGIN_KEY = "LOGIN"
        private const val PASSWORD_KEY = "PASS"
        private const val TOKEN_KEY = "TOKEN"
        private var cachedUser: UserData? = null

    }
}