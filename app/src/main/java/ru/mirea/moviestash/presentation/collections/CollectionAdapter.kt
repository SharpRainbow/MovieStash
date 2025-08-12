package ru.mirea.moviestash.presentation.collections

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemColBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity

class CollectionAdapter(
    private val personal: Boolean = false
) : ListAdapter<CollectionEntity, CollectionAdapter.ColViewHolder>(
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
        val collection = getItem(position)
        holder.binding.colName.text = collection.name
        if (personal) {
            holder.itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            holder.binding.colName.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
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