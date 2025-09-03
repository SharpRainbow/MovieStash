package ru.mirea.moviestash.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mirea.moviestash.data.api.MovieStashApi
import ru.mirea.moviestash.data.api.dto.CredentialsDto
import ru.mirea.moviestash.data.api.dto.RegisterDto
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.AuthRepository
import javax.inject.Inject

private const val PREFERENCE_NAME = "authentication"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

@ApplicationScope
class AuthRepositoryImpl @Inject constructor(
    private val application: Application,
    private val movieStashApi: MovieStashApi
): AuthRepository {

    private val context: Context = application.applicationContext

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

    override suspend fun login(login: String, password: String): String {
        return movieStashApi.login(
            CredentialsDto(
                login = login,
                password = password
            )
        ).token
    }

    override suspend fun saveCredentials(login: String, password: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = login
            preferences[PASSWORD_KEY] = password
            preferences[TOKEN_KEY] = token
        }
    }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    override suspend fun logout() {
        context.dataStore.edit {
            it.clear()
        }
    }

    override fun getToken(): Flow<String> {
        return context.dataStore.data.map { it[TOKEN_KEY] ?: "" }
    }

    override fun getSavedLogin(): Flow<String> {
        return context.dataStore.data.map { it[LOGIN_KEY] ?: "" }
    }

    override fun getSavedPassword(): Flow<String> {
        return context.dataStore.data.map { it[PASSWORD_KEY] ?: "" }
    }

    companion object {
        private val LOGIN_KEY = stringPreferencesKey("LOGIN")
        private val PASSWORD_KEY = stringPreferencesKey("PASS")
        private val TOKEN_KEY = stringPreferencesKey("TOKEN")

    }
}