package ru.mirea.moviestash.collections

import androidx.lifecycle.ViewModel
import ru.mirea.moviestash.entites.Collection

class CollectionsModel : ViewModel() {

    private val cols by lazy {
        mutableListOf<Collection>()
    }

    fun getSize() = cols.size

    fun isEmpty() = cols.isEmpty()

    fun add(collection: Collection) = cols.add(collection)

    fun addAll(collections: Iterable<Collection>) = cols.addAll(collections)

    fun getAll() = cols

    fun clear() = cols.clear()

}