package ru.mirea.moviestash.news

import androidx.lifecycle.ViewModel
import ru.mirea.moviestash.entites.News

class NewsModel : ViewModel() {

    private val news: MutableList<News> by lazy {
        mutableListOf()
    }
    var offset = 0

    fun getSize() = news.size

    fun isEmpty() = news.isEmpty()

    fun add(news: News) = this.news.add(news)

    fun addAll(news: Iterable<News>) = this.news.addAll(news)

    fun getAll() = news

    fun clear() = news.clear()
}