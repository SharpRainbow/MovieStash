package ru.mirea.moviestash.user_management

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.mirea.moviestash.entites.Credentials

@Dao
interface CredsDao {

    @Query("SELECT * FROM credentials")
    fun getAll(): List<Credentials>

    @Query("SELECT * FROM credentials WHERE username = :username")
    fun getByLogin(username: String): List<Credentials>

    @Insert
    fun insertCred(credentials: Credentials)

    @Update
    fun update(credentials: Credentials): Int

    @Delete
    fun remove(credentials: Credentials)

}