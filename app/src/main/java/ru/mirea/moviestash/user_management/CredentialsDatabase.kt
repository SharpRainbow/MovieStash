package ru.mirea.moviestash.user_management

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mirea.moviestash.entites.Credentials

@Database(entities = [Credentials::class], version = 1)
abstract class CredentialsDatabase : RoomDatabase() {

    abstract fun credsDao(): CredsDao

    companion object {
        private var Instance: CredentialsDatabase? = null

        fun getDatabase(context: Context): CredentialsDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(
                    context, CredentialsDatabase::class.java, "credentials_database"
                ).build().also { Instance = it }
            }
        }
    }
}