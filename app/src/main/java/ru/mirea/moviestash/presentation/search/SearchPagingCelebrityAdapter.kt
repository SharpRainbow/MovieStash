package ru.mirea.moviestash.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.databinding.ItemSearchedBinding
import ru.mirea.moviestash.domain.entities.CelebrityEntityBase

class SearchPagingCelebrityAdapter :
    PagingDataAdapter<CelebrityEntityBase, SearchPagingCelebrityAdapter.SearchViewHolder>(
        CelebrityBaseDiffCallback()
    ) {

    var onCelebrityClick: ((CelebrityEntityBase) -> Unit)? = null

    class SearchViewHolder(
        val binding: ItemSearchedBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SearchPagingCelebrityAdapter.SearchViewHolder {
        val binding = ItemSearchedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchPagingCelebrityAdapter.SearchViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SearchPagingCelebrityAdapter.SearchViewHolder,
        position: Int
    ) {
        getItem(position)?.let { celebrity ->
            with(holder.binding) {
                textViewSearchName.text = celebrity.name
                textViewSearchName.text = celebrity.birthDate
                Glide
                    .with(imageViewSearchImage)
                    .load(celebrity.image)
                    .into(imageViewSearchImage)
            }
            holder.itemView.setOnClickListener {
                onCelebrityClick?.invoke(celebrity)
            }
        }
    }
}