package ru.mirea.moviestash.content

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.mirea.moviestash.BR
import java.sql.Date

@Parcelize
data class Content(val id: Int, var name: String, val description: String, val budget: Long?,
    val boxOffice: Long?, val duration: String?, private var _rating: Float = 0f, var image: String?,
    var releaseDate: Date) : BaseObservable(), Parcelable{

    @IgnoredOnParcel
    private var _genres: String = ""
    @IgnoredOnParcel
    private var _countries: String = ""
    @IgnoredOnParcel
    var bmp: Bitmap? = null

    var genres: String
        @Bindable get() = _genres
        set(value) {
            _genres = value
            notifyPropertyChanged(BR.genres)
        }

    var countries: String
        @Bindable get() = _countries
        set(value) {
            _countries = value
            notifyPropertyChanged(BR.countries)
        }

    var rating: Float
        @Bindable get() = _rating
        set(value) {
            _rating = value
            notifyPropertyChanged(BR.rating)
        }

}

data class FilmRating(val ratingKinopoisk: String?, val ratingImdb: String?)

data class Movie(var filmId: String?, var nameRu: String?)