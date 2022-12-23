package ru.mirea.moviestash.news

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
data class News(val id: Int, val title: String, val description: String, val date: Date,
                val img: String?): Parcelable {
    @IgnoredOnParcel
    var bmp: Bitmap? = null
}