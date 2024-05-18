package ru.mirea.moviestash.entites

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "user_stars")
class UserStar {
    @DatabaseField(columnName = "sid", id = true)
    val id: Int = 0

    @DatabaseField(columnName = "content_id", foreign = true)
    val content: Content? = null

    @DatabaseField(columnName = "uid", foreign = true)
    val user: SiteUser? = null

    @DatabaseField
    val rating: Int = 0
}