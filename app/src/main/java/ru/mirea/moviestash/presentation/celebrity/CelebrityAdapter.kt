package ru.mirea.moviestash.presentation.celebrity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.data.api.dto.CelebrityInContentDto
import ru.mirea.moviestash.databinding.ItemPersonBinding

class CelebrityAdapter :
    ListAdapter<CelebrityInContentDto, CelebrityAdapter.CelebrityViewHolder>(
        CelebrityDiffCallback()
    ) {

    class CelebrityViewHolder(
        val binding: ItemPersonBinding
    ): RecyclerView.ViewHolder(binding.root)

    var onCelebrityClick: ((CelebrityInContentDto) -> Unit)? = null
    var onReachEndListener: (() -> Unit)? = null

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
        holder.binding.textViewPersonName.text = person.name
        holder.binding.textViewPersonDescription.text = person.description.ifEmpty {
            person.role
        }
        Glide
            .with(holder.binding.personImage)
            .load(person.image)
            .into(holder.binding.personImage)
        holder.itemView.setOnClickListener {
            onCelebrityClick?.invoke(person)
        }
        if (position >= itemCount - 5) {
            onReachEndListener?.invoke()
        }
    }
}