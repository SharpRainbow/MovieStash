package ru.mirea.moviestash.celebrities

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
data class Celebrity(val id: Int, var name: String, var height: Int? = null,
                     var birthday: Date? = null, var death: Date? = null,
                     var birthplace: String? = null, var career: String? = null,
                     var img: String? = null) : Parcelable

data class CelebrityInContent(val id: Int, val name: String, val role: String?,
                              var desc: String?, val img: String?){
    var imageBitmap: Bitmap? = null
}