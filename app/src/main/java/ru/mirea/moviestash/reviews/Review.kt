package ru.mirea.moviestash.reviews

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
data class Review(val id: Int, var desc: String, val date : Date, val userId: Int,
    var title: String, var opinion: String, val userName: String): Parcelable
