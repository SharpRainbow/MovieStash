package ru.mirea.moviestash.entites

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "countries_of_content")
class CountryOfContent {
    @DatabaseField(columnName = "content_id", foreign = true)
    lateinit var content: Content

    @DatabaseField(columnName = "country_id", foreign = true)
    lateinit var country: Country
}