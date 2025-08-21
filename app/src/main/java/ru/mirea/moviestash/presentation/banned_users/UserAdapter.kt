package ru.mirea.moviestash.presentation.banned_users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.ItemUserBinding
import ru.mirea.moviestash.domain.entities.BannedUserEntity

class BannedUserAdapter
    : PagingDataAdapter<BannedUserEntity, BannedUserAdapter.UserViewHolder>(
    BannedUserDiffCallback()
    ) {

        var onUserClick: ((View, BannedUserEntity) -> Unit)? = null

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { bannedUser ->
            with(holder.binding) {
                textViewCollectionDescription.isSingleLine = false
                textViewCollectionName.text = "${bannedUser.nickname} ${bannedUser.email}"
                textViewCollectionDescription.text =
                    holder.itemView.context.getString(
                        R.string.banned_reason_label,
                        bannedUser.banDate,
                        bannedUser.banReason
                    )
            }
            holder.itemView.setOnClickListener {
                onUserClick?.invoke(holder.itemView, bannedUser)
            }
        }
    }

}