package ru.mirea.moviestash.presentation.collections

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemColBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity

class CollectionPagedAdapter(
    private val personal: Boolean = false
) : PagingDataAdapter<CollectionEntity, CollectionPagedAdapter.ColViewHolder>(
    CollectionDiffCallback()
) {

    var onCollectionClick: ((CollectionEntity) -> Unit)? = null
    var onCollectionLongClick: ((View, CollectionEntity) -> Unit)? = null

    class ColViewHolder(val binding: ItemColBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColViewHolder {
        val itemView = ItemColBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ColViewHolder, position: Int) {
        getItem(position)?.let { collection ->
            holder.binding.textViewCollectionName.text = collection.name
            if (personal) {
                holder.itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                holder.binding.textViewCollectionName.layoutParams.width =
                    ViewGroup.LayoutParams.MATCH_PARENT
            }
            holder.itemView.setOnClickListener {
                onCollectionClick?.invoke(collection)
            }
            holder.itemView.setOnLongClickListener {
                onCollectionLongClick?.invoke(holder.itemView, collection)
                true
            }
        }
    }

}