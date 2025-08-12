package ru.mirea.moviestash.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CredsDao {

    @Query("SELECT * FROM credentials")
    fun getAll(): Flow<List<CredentialsDbModel>>

    @Query("SELECT * FROM credentials WHERE login = :username LIMIT 1")
    suspend fun getByLogin(username: String): CredentialsDbModel?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCred(credentials: CredentialsDbModel)

    @Query("DELETE FROM credentials WHERE login = :login")
    suspend fun deleteByLogin(login: String)

}