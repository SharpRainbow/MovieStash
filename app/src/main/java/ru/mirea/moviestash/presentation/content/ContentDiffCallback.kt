package ru.mirea.moviestash.presentation.content

import androidx.recyclerview.widget.DiffUtil
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase

class ContentDiffCallback: DiffUtil.ItemCallback<ContentEntityBase>() {

    override fun areItemsTheSame(
        oldItem: ContentEntityBase,
        newItem: ContentEntityBase
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ContentEntityBase,
        newItem: ContentEntityBase
    ): Boolean {
        return oldItem == newItem
    }
}