package ru.mirea.moviestash.entites

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "genres_of_content")
class GenreOfContent {
    @DatabaseField(columnName = "content_id", foreign = true)
    lateinit var content: Content

    @DatabaseField(columnName = "genre_id", foreign = true)
    lateinit var genre: Genre
}