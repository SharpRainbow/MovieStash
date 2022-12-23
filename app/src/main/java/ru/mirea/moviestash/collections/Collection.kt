package ru.mirea.moviestash.collections

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Collection(val id: Int, val name: String, var uid: Int = -1, var description: String = "") :
    Parcelable
