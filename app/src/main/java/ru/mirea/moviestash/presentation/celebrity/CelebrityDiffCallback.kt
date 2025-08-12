package ru.mirea.moviestash.presentation.celebrity

import androidx.recyclerview.widget.DiffUtil
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto

class CelebrityDiffCallback: DiffUtil.ItemCallback<CelebrityInContentDto>() {

    override fun areItemsTheSame(
        oldItem: CelebrityInContentDto,
        newItem: CelebrityInContentDto
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CelebrityInContentDto,
        newItem: CelebrityInContentDto
    ): Boolean {
        return oldItem == newItem
    }
}