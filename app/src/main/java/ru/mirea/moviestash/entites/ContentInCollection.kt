package ru.mirea.moviestash.entites

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "content_in_collection")
data class ContentInCollection(
    @DatabaseField(columnName = "film_number")
    val filmNumber: Int = 0
) {
    @DatabaseField(columnName = "content_id", foreign = true)
    lateinit var content: Content

    @DatabaseField(columnName = "collection_id", foreign = true)
    lateinit var collection: Collection

    override fun toString(): String {
        return "ContentInCollection(filmNumber=$filmNumber, content=$content, collection=$collection)"
    }

}
