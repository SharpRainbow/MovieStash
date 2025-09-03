package ru.mirea.moviestash.data.database

import android.content.Context
import androidx.annotation.GuardedBy
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CredentialsDbModel::class], version = 1, exportSchema = false)
abstract class CredentialsDatabase : RoomDatabase() {

    abstract fun credentialsDao(): CredentialsDao

    companion object {

        private val lock = Any()

        @Volatile
        @GuardedBy("lock")
        private var INSTANCE: CredentialsDatabase? = null
        private const val DB_NAME = "credentials.db"

        fun getDatabase(context: Context): CredentialsDatabase {
            return INSTANCE ?: synchronized(lock) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    CredentialsDatabase::class.java,
                    DB_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}