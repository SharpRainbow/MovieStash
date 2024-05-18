package ru.mirea.moviestash.entites

import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "best_films")
class BestFilms : Content()