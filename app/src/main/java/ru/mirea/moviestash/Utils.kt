package ru.mirea.moviestash

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

object Utils {

    fun getLiveDates(a: String, b: String): String {
        return if (b.isEmpty()) {
            String.format("Дата рождения: %s", a)
        } else {
            String.format("Годы жизни:\n%s - %s", a, b)
        }
    }

    fun <T> Flow<T>.mergeWith(another: Flow<T>): Flow<T> {
        return merge(this, another)
    }
}
