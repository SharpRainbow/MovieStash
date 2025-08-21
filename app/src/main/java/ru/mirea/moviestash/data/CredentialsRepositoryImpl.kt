package ru.mirea.moviestash.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mirea.moviestash.data.database.CredentialsDbModel
import ru.mirea.moviestash.data.database.CredentialsDao
import ru.mirea.moviestash.data.mappers.toEntity
import ru.mirea.moviestash.data.mappers.toEntityList
import ru.mirea.moviestash.di.ApplicationScope
import ru.mirea.moviestash.domain.CredentialsRepository
import ru.mirea.moviestash.domain.entities.CredentialsEntity
import javax.inject.Inject

@ApplicationScope
class CredentialsRepositoryImpl @Inject constructor(
    private val credentialsDao: CredentialsDao
) : CredentialsRepository {

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