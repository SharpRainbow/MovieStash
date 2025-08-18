package ru.mirea.moviestash.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.databinding.ItemSearchedBinding
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.presentation.content.ContentDiffCallback

class SearchPagingContentAdapter :
    PagingDataAdapter<ContentEntityBase, SearchPagingContentAdapter.SearchViewHolder>(
        ContentDiffCallback()
    ) {

    var onContentClick: ((ContentEntityBase) -> Unit)? = null

    class SearchViewHolder(
        val binding: ItemSearchedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        getItem(position)?.let { content ->
            holder.binding.searchDesc.text = content.releaseDate
            holder.binding.searchName.text = content.name
            Glide
                .with(holder.binding.searchImage)
                .load(content.image)
                .into(holder.binding.searchImage)
            holder.itemView.setOnClickListener {
                onContentClick?.invoke(content)
            }
        }
    }

}