package ru.mirea.moviestash.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toEntityList
import ru.mirea.moviestash.domain.CredentialsRepository
import ru.mirea.moviestash.domain.entities.CredentialsEntity
import ru.mirea.moviestash.data.database.CredentialsDbModel
import ru.mirea.moviestash.data.database.CredentialsDatabase
import kotlin.getValue

class CredentialsRepositoryImpl(
    private val context: Context
) : CredentialsRepository {

    private val credentialsDao by lazy {
        CredentialsDatabase.getDatabase(context).credsDao()
    }

    override fun getCredentials(): Flow<List<CredentialsEntity>> {
        return credentialsDao.getAll().map {
            it.toEntityList()
        }
    }

    override suspend fun addCredentials(
        login: String,
        password: String,
    ) {
        credentialsDao.insertCred(
            CredentialsDbModel(
                login = login,
                password = password,
            )
        )
    }

    override suspend fun getByLogin(login: String): CredentialsEntity? {
        return credentialsDao.getByLogin(login)?.toEntity()
    }

    override suspend fun removeCredential(login: String) {
        credentialsDao.deleteByLogin(login)
    }
}