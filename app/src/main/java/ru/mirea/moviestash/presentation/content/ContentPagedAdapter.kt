package ru.mirea.moviestash.presentation.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.ItemContentBinding
import ru.mirea.moviestash.domain.entities.ContentEntityBase

class ContentPagedAdapter :
    PagingDataAdapter<ContentEntityBase, ContentPagedAdapter.ContentViewHolder>(
        ContentDiffCallback()
    ) {

    var onContentClick: ((ContentEntityBase) -> Unit)? = null
    var onContentLongClick: ((View, ContentEntityBase) -> Unit)? = null

    class ContentViewHolder(
        val binding: ItemContentBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding = ItemContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        getItem(position)?.let { content ->
            holder.binding.textViewName.text = content.name
            Glide
                .with(holder.binding.imageViewContentImage)
                .load(content.image)
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.imageViewContentImage)
            holder.itemView.setOnClickListener {
                onContentClick?.invoke(content)
            }
            holder.itemView.setOnLongClickListener {
                onContentLongClick?.invoke(holder.itemView, content)
                true
            }
        }
    }

}