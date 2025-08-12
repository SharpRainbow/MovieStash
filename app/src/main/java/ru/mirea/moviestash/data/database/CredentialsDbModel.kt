package ru.mirea.moviestash.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credentials")
data class CredentialsDbModel(
    @PrimaryKey
    val login: String,
    val password: String
)