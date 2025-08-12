package ru.mirea.moviestash

sealed class Result<out R> {
    data object Empty : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}