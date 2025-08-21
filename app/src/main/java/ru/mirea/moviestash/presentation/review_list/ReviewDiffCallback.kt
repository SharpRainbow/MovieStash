package ru.mirea.moviestash.presentation.review_list

import androidx.recyclerview.widget.DiffUtil
import ru.mirea.moviestash.domain.entities.ReviewEntity

class ReviewDiffCallback: DiffUtil.ItemCallback<ReviewEntity>() {

    override fun areItemsTheSame(
        oldItem: ReviewEntity,
        newItem: ReviewEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ReviewEntity,
        newItem: ReviewEntity
    ): Boolean {
        return oldItem == newItem
    }
}