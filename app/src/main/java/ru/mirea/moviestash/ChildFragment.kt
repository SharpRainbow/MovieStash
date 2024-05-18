package ru.mirea.moviestash

interface ChildFragment {

    suspend fun loadContent(refresh: Boolean = true): Boolean

    fun isInitialized(): Boolean

}