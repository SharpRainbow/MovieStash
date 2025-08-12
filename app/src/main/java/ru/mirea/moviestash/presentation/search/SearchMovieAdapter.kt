package ru.mirea.moviestash.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.presentation.content.ContentDiffCallback
import ru.mirea.moviestash.databinding.ItemSearchedBinding
import ru.mirea.moviestash.domain.entities.ContentEntityBase

class SearchMovieAdapter :
    ListAdapter<ContentEntityBase, SearchMovieAdapter.SearchViewHolder>(
        ContentDiffCallback()
    ) {

        var onContentClick: ((ContentEntityBase) -> Unit)? = null
    var onListEndReached: (() -> Unit)? = null

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
        val content = getItem(position)
        holder.binding.searchName.text = content.name
        holder.binding.searchDesc.text = content.releaseDate
        Glide
            .with(holder.binding.searchImage)
            .load(content.image)
            .into(holder.binding.searchImage)
        holder.itemView.setOnClickListener {
            onContentClick?.invoke(content)
        }
        if (itemCount > 5 && position >= itemCount - 5) {
            onListEndReached?.invoke()
        }
    }

}