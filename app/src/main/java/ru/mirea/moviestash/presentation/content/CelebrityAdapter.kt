package ru.mirea.moviestash.presentation.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.databinding.ItemPersonBinding
import ru.mirea.moviestash.domain.entities.CelebrityInContentEntity
import ru.mirea.moviestash.presentation.celebrity_list.CelebrityDiffCallback

class CelebrityAdapter :
    ListAdapter<CelebrityInContentEntity, CelebrityAdapter.CelebrityViewHolder>(
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
        val person = getItem(position)
        with(holder.binding) {
            textViewPersonName.text = person.name
            textViewPersonDescription.text = person.description.ifEmpty {
                person.role
            }
            Glide
                .with(imageViewPersonImage)
                .load(person.image)
                .into(imageViewPersonImage)
        }
        holder.itemView.setOnClickListener {
            onCelebrityClick?.invoke(person)
        }
    }
}