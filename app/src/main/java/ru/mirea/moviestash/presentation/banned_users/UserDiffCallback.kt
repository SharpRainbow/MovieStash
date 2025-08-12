package ru.mirea.moviestash.presentation.banned_users

import androidx.recyclerview.widget.DiffUtil
import ru.mirea.moviestash.domain.entities.BannedUserEntity

class BannedUserDiffCallback: DiffUtil.ItemCallback<BannedUserEntity>() {

    override fun areItemsTheSame(
        oldItem: BannedUserEntity,
        newItem: BannedUserEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: BannedUserEntity,
        newItem: BannedUserEntity
    ): Boolean {
        return oldItem == newItem
    }
}