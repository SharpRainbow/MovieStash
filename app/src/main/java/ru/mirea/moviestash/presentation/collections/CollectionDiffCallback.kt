package ru.mirea.moviestash.presentation.collections

import androidx.recyclerview.widget.DiffUtil
import ru.mirea.moviestash.domain.entities.CollectionEntity

class CollectionDiffCallback: DiffUtil.ItemCallback<CollectionEntity>() {

    override fun areItemsTheSame(
        oldItem: CollectionEntity,
        newItem: CollectionEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CollectionEntity,
        newItem: CollectionEntity
    ): Boolean {
        return oldItem == newItem
    }
}