package ru.mirea.moviestash.entites

import android.graphics.Bitmap
import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
@DatabaseTable(tableName = "news")
open class News(
    @DatabaseField(columnName = "nid", id = true)
    val id: Int = 0,
    @DatabaseField
    val description: String = "",
    @DatabaseField
    val title: String = "",
    @DatabaseField(columnName = "news_date")
    val date: Date? = null,
    @DatabaseField(columnName = "uid", foreign = true)
    val user: SiteUser? = null,
    @DatabaseField(columnName = "image_link", canBeNull = true)
    val image: String? = null
) : Parcelable {
    @IgnoredOnParcel
    var bmp: Bitmap? = null
}