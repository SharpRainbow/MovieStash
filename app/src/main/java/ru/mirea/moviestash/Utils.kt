package ru.mirea.moviestash

object Utils {

    fun getLiveDates(a: String, b: String): String {
        return if (b.isEmpty()) {
            String.format("Дата рождения: %s", a)
        } else {
            String.format("Годы жизни:\n%s - %s", a, b)
        }
    }
}
