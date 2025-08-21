package ru.mirea.moviestash.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CredentialsDbModel::class], version = 1, exportSchema = false)
abstract class CredentialsDatabase : RoomDatabase() {

    abstract fun credsDao(): CredentialsDao

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