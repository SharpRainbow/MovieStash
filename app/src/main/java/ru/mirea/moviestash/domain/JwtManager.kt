package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.first
import org.json.JSONObject
import ru.mirea.moviestash.domain.entities.Role
import ru.mirea.moviestash.domain.entities.UserData
import ru.mirea.moviestash.domain.usecases.user.GetSavedPasswordUseCase
import ru.mirea.moviestash.domain.usecases.user.GetTokenUseCase
import ru.mirea.moviestash.domain.usecases.user.SaveTokenUseCase
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class JwtManager @Inject constructor(
    private val authRepository: AuthRepository,
    private val getSavedPasswordUseCase: GetSavedPasswordUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) {

    suspend fun getValidToken(): String {
        val token = getTokenUseCase().first()
        if (isTokenValid(token)) {
            return token
        } else {
            val login = authRepository.getSavedLogin().first()
            val password = getSavedPasswordUseCase().first()
            if (login.isNotBlank() && password.isNotBlank()) {
                val newToken = authRepository.login(login, password)
                saveTokenUseCase(newToken)
            }
            return getTokenUseCase().first()
        }
    }

    fun parseJwtToken(token: String): UserData {
        val payload = JSONObject(
            String(
                Base64.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)
                    .decode(token.split('.')[1])
            )
        )
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

    fun isTokenValid(token: String): Boolean {
        return try {
            val payload = JSONObject(
                String(
                    Base64.withPadding(
                        Base64.PaddingOption.PRESENT_OPTIONAL
                    ).decode(token.split('.')[1])
                )
            )
            payload.getLong("exp") > System.currentTimeMillis() / 1000
        } catch (e: Exception) {
            false
        }
    }

}