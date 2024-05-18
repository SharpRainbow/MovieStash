package ru.mirea.moviestash.entites

import android.graphics.Bitmap
import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
@DatabaseTable(tableName = "celebrity")
class Celebrity(
    @DatabaseField(columnName = "cid", id = true)
    var id: Int = 0,
    @DatabaseField
    var name: String = "",
    @DatabaseField(canBeNull = true)
    var height: Int? = null,
    @DatabaseField(canBeNull = true)
    var birthday: Date? = null,
    @DatabaseField(canBeNull = true)
    var death: Date? = null,
    @DatabaseField(canBeNull = true)
    var birthplace: String? = null,
    @DatabaseField(canBeNull = true)
    var career: String? = null,
    @DatabaseField(canBeNull = true, columnName = "img_link")
    var img: String? = null,
) : Parcelable {
    @IgnoredOnParcel
    var role: String = ""

    @IgnoredOnParcel
    var description: String = ""

    @IgnoredOnParcel
    var bmp: Bitmap? = null

    override fun toString(): String {
        return "Celebrity(id=$id, name='$name', height=$height, birthday=$birthday, death=$death, birthplace=$birthplace, career=$career, img=$img)"
    }

}