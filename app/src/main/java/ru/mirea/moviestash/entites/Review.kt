package ru.mirea.moviestash.entites

import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
@DatabaseTable(tableName = "review")
class Review(
    @DatabaseField(columnName = "rid")
    val id: Int = 0,
    @DatabaseField
    val description: String = "",
    @DatabaseField(columnName = "rev_date")
    val date: Date? = null,
    @DatabaseField
    val title: String = "",
    @DatabaseField
    val opinion: Int = 0,
    @DatabaseField(columnName = "content_id", foreign = true)
    var content: Content? = null,
    @DatabaseField(columnName = "uid", foreign = true)
    var user: SiteUser? = null
) : Parcelable {

    override fun toString(): String {
        return "Review(id=$id, description='$description', date='$date', title='$title', opinion=$opinion, content=$content, user=$user)"
    }

}
