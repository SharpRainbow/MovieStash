package ru.mirea.moviestash.presentation.news

import androidx.recyclerview.widget.DiffUtil
import ru.mirea.moviestash.domain.entities.NewsEntity

class NewsDiffCallback: DiffUtil.ItemCallback<NewsEntity>() {

    override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
        return oldItem == newItem
    }
}