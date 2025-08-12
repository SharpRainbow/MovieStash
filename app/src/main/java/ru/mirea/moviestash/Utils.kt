package ru.mirea.moviestash

import java.sql.Date
import java.sql.Time
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {
    fun dateToString(date: Date?): String {
        if (date == null) return ""
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
    }

    fun timeToString(time: Time): String {
        return SimpleDateFormat("HH ч mm мин", Locale.getDefault()).format(time)
    }

    fun formatMoney(money: Long): String {
        return DecimalFormat("###,###").format(money) + " $"
    }

    fun getLiveDates(b: Date?, d: Date?): String? {
        if (b == null && d == null) {
            return null
        } else if (b == null) return String.format("Дата смерти: %s", dateToString(d))
        else if (d == null) return String.format("Дата рождения: %s", dateToString(b))
        else return String.format("Годы жизни:\n%s - %s", dateToString(b), dateToString(d))
    }

    fun getLiveDates(a: String, b: String): String {
        return if (b.isEmpty()) {
            String.format("Дата рождения: %s", a)
        } else {
            String.format("Годы жизни:\n%s - %s", a, b)
        }
    }

    fun opinionCodeToString(code: Int): String {
        var op = "Нейтральный отзыв"
        when (code) {
            1 -> op = "Оставил негативный отзыв"
            2 -> op = "Оставил нейтральный отзыв"
            3 -> op = "Оставил позитивный отзыв"
        }
        return op
    }
}
