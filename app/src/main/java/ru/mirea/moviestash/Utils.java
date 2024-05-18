package ru.mirea.moviestash;

import java.sql.Date;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Utils {

    public static String dateToString(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    public static String timeToString(Time time) {
        return new SimpleDateFormat("HH ч mm мин").format(time);
    }

    public static String formatMoney(long money) {
        return new DecimalFormat("###,###").format(money) + " $";
    }

    public static String getLiveDates(Date b, Date d) {
        if (b == null && d == null) {
            return null;
        } else if (b == null) return String.format("Дата смерти: %s", dateToString(d));
        else if (d == null) return String.format("Дата рождения: %s", dateToString(b));
        else return String.format("Годы жизни:\n%s - %s", dateToString(b), dateToString(d));

    }

    public static String opinionCodeToString(int code) {
        String op = "Нейтральный отзыв";
        switch (code) {
            case 1:
                op = "Оставил негативный отзыв";
                break;
            case 2:
                op = "Оставил нейтральный отзыв";
                break;
            case 3:
                op = "Оставил позитивный отзыв";
        }
        return op;
    }
}
