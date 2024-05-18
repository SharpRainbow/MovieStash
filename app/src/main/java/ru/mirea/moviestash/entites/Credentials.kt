package ru.mirea.moviestash.entites

import androidx.room.Entity

@Entity(tableName = "credentials", primaryKeys = ["username", "password"])
data class Credentials(
    val username: String,
    val password: String,
    var email: String
)
