package ru.mirea.moviestash.entites

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.mirea.moviestash.BR
import java.sql.Date

@Parcelize
@DatabaseTable(tableName = "content")
open class Content(
    @DatabaseField(columnName = "content_id", id = true)
    val id: Int = 0,
    @DatabaseField
    val name: String = "",
    @DatabaseField
    val description: String = "",
    @DatabaseField(canBeNull = true)
    val budget: Long? = null,
    @DatabaseField(canBeNull = true, columnName = "box_office")
    val boxOffice: Long? = null,
    @DatabaseField
    val duration: String = "",
    @DatabaseField(canBeNull = true, columnName = "image_link")
    val image: String? = null,
    @DatabaseField(columnName = "release_date")
    val released: Date? = null,
    @DatabaseField(columnName = "rating")
    private var _rating: Float = 0f
) : Parcelable, BaseObservable() {

    var rating: Float
        @Bindable get() = _rating
        set(value) {
            _rating = value
            notifyPropertyChanged(BR.rating)
        }

    @IgnoredOnParcel
    var bmp: Bitmap? = null

    @IgnoredOnParcel
    var genres: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.genres)
        }

    @IgnoredOnParcel
    var countries: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.countries)
        }

}