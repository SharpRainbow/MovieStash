package ru.mirea.moviestash

import androidx.lifecycle.ViewModel
import ru.mirea.moviestash.entites.Collection
import ru.mirea.moviestash.entites.Content
import ru.mirea.moviestash.entites.News

class HomeDataModel : ViewModel() {

    private val films by lazy {
        mutableListOf<Content>()
    }
    private val collections by lazy {
        mutableListOf<Collection>()
    }
    private val news by lazy {
        mutableListOf<News>()
    }

    fun getFilmsSize() = films.size

    fun isFilmsEmpty() = films.isEmpty()

    fun addFilm(film: Content) = films.add(film)

    fun addAllFilms(films: Iterable<Content>) = this.films.addAll(films)

    fun getAllFilm() = films

    fun clearFilms() = films.clear()

    fun getColsSize() = collections.size

    fun isColsEmpty() = collections.isEmpty()

    fun addCol(collection: Collection) = collections.add(collection)

    fun addAllCols(collections: Iterable<Collection>) = this.collections.addAll(collections)

    fun getAllCols() = collections

    fun clearCols() = collections.clear()

    fun getNewsSize() = news.size

    fun isNewsEmpty() = news.isEmpty()

    fun addNew(new: News) = news.add(new)

    fun addAllNews(news: Iterable<News>) = this.news.addAll(news)

    fun getAllNews() = news

    fun clearNews() = news.clear()

    fun isDataEmpty() = isFilmsEmpty() && isColsEmpty() && isNewsEmpty()
}