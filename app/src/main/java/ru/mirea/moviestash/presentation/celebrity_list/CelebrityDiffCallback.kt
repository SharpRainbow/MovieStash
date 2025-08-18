package ru.mirea.moviestash.presentation.celebrity_list

import androidx.recyclerview.widget.DiffUtil
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity

class CelebrityDiffCallback: DiffUtil.ItemCallback<CelebrityInContentEntity>() {

    override fun areItemsTheSame(
        oldItem: CelebrityInContentEntity,
        newItem: CelebrityInContentEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CelebrityInContentEntity,
        newItem: CelebrityInContentEntity
    ): Boolean {
        return oldItem == newItem
    }
}