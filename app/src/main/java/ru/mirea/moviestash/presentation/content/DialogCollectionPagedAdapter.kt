package ru.mirea.moviestash.presentation.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemUserCollectionBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.presentation.collections.CollectionDiffCallback

class DialogCollectionPagedAdapter :
    PagingDataAdapter<CollectionEntity, DialogCollectionPagedAdapter.ColViewHolder>(
        CollectionDiffCallback()
    ) {

    var onCollectionClick: ((CollectionEntity) -> Unit)? = null

    class ColViewHolder(val binding: ItemUserCollectionBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColViewHolder {
        val itemView = ItemUserCollectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ColViewHolder, position: Int) {
        getItem(position)?.let { collection ->
            with(holder.binding) {
                textViewCollectionName.text = collection.name
                textViewCollectionDescription.text = collection.description
            }
            holder.itemView.setOnClickListener {
                onCollectionClick?.invoke(collection)
            }
        }
    }
}