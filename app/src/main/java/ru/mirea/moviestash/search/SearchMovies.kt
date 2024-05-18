package ru.mirea.moviestash.search

import ru.mirea.moviestash.content.Movie

data class SearchMovies(var keyword: String?, var films: ArrayList<Movie>?)
