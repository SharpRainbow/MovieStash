package ru.mirea.moviestash.entites

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "genre")
data class Genre(
    @DatabaseField(columnName = "genre_id", id = true)
    val id: Int = 0,
    @DatabaseField
    val name: String = ""
)
