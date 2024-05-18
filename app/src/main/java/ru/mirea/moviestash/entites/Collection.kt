package ru.mirea.moviestash.entites

import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.parcelize.Parcelize

@Parcelize
@DatabaseTable(tableName = "collection")
data class Collection(
    @DatabaseField(columnName = "collection_id", id = true)
    val id: Int = 0,
    @DatabaseField
    val name: String = "",
    @DatabaseField
    val description: String = "",
    @DatabaseField(canBeNull = true)
    val uid: Int? = null
) : Parcelable
