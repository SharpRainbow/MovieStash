package ru.mirea.moviestash.entites

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "country")
data class Country(
    @DatabaseField(columnName = "country_id", id = true)
    val id: Int = 0,
    @DatabaseField
    val name: String = ""
)
