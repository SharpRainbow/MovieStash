package ru.mirea.moviestash.entites

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "celebrity_in_content")
class CelebrityInContent {
    @DatabaseField(foreign = true, columnName = "content_id")
    lateinit var content: Content

    @DatabaseField(foreign = true, columnName = "cid")
    lateinit var celebrity: Celebrity

    @DatabaseField(columnName = "role", foreign = true, foreignColumnName = "role_id")
    lateinit var role: RoleClassifier

    @DatabaseField(canBeNull = true)
    val description: String? = null

    @DatabaseField(canBeNull = true)
    val priority: Int? = null

    override fun toString(): String {
        return "CelebrityInContent(content=${content}, celebrity=${celebrity}, role=$role, description=$description, priority=$priority)"
    }

}