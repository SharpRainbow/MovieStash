package ru.mirea.moviestash.presentation.search

import androidx.recyclerview.widget.DiffUtil
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase

class CelebrityBaseDiffCallback: DiffUtil.ItemCallback<CelebrityEntityBase>() {

    override fun areItemsTheSame(
        oldItem: CelebrityEntityBase,
        newItem: CelebrityEntityBase
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CelebrityEntityBase,
        newItem: CelebrityEntityBase
    ): Boolean {
        return oldItem == newItem
    }
}