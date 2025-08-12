package ru.mirea.moviestash.presentation.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import ru.mirea.moviestash.databinding.ItemSearchedBinding
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase

class SearchCelebrityAdapter :
    ListAdapter<CelebrityEntityBase, SearchMovieAdapter.SearchViewHolder>(
        CelebrityBaseDiffCallback()
    ) {

        var onCelebrityClick: ((CelebrityEntityBase) -> Unit)? = null

    var onListEndReached: (() -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SearchMovieAdapter.SearchViewHolder {
        val binding = ItemSearchedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchMovieAdapter.SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchMovieAdapter.SearchViewHolder, position: Int) {
        val celebrity = getItem(position)
        holder.binding.searchName.text = celebrity.name
        holder.binding.searchDesc.text = celebrity.birthDate
        Glide
            .with(holder.binding.searchImage)
            .load(celebrity.image)
            .into(holder.binding.searchImage)
        holder.itemView.setOnClickListener {
            onCelebrityClick?.invoke(celebrity)
        }
        if (position >= itemCount - 5) {
            onListEndReached?.invoke()
        }
    }
}