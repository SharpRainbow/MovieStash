package ru.mirea.moviestash.domain

import kotlinx.coroutines.flow.Flow
import ru.mirea.moviestash.domain.entities.CredentialsEntity

interface CredentialsRepository {

    fun getCredentials(): Flow<List<CredentialsEntity>>

    suspend fun addCredentials(login: String, password: String)

    suspend fun getByLogin(login: String): CredentialsEntity?

    suspend fun removeCredential(login: String)
}