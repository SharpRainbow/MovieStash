package ru.mirea.moviestash.presentation.celebrity_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.databinding.ItemPersonBinding
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity
import ru.mirea.moviestash.presentation.celebrity_list.CelebrityDiffCallback

class CelebrityPagingAdapter :
    PagingDataAdapter<CelebrityInContentEntity, CelebrityPagingAdapter.CelebrityViewHolder>(
        CelebrityDiffCallback()
    ) {

    class CelebrityViewHolder(
        val binding: ItemPersonBinding
    ): RecyclerView.ViewHolder(binding.root)

    var onCelebrityClick: ((CelebrityInContentEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CelebrityViewHolder {
        val binding = ItemPersonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CelebrityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CelebrityViewHolder, position: Int) {
        getItem(position)?.let { person ->
            with(holder.binding) {
                Glide
                    .with(imageViewPersonImage)
                    .load(person.image)
                    .into(imageViewPersonImage)
                textViewPersonName.text = person.name
                textViewPersonDescription.text = person.description.ifEmpty {
                    person.role
                }
            }
            holder.itemView.setOnClickListener {
                onCelebrityClick?.invoke(person)
            }
        }
    }
}