package ru.mirea.moviestash.presentation.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.ItemNewsBinding
import ru.mirea.moviestash.domain.entities.NewsEntity

class NewsAdapter :
    ListAdapter<NewsEntity, NewsAdapter.NewsViewHolder>(
        NewsDiffCallback()
    ) {

        var onNewsClick: ((NewsEntity) -> Unit)? = null

    class NewsViewHolder(
        val binding: ItemNewsBinding
    ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = getItem(position)
        holder.binding.textViewTitleItemNews.text = news.title
        holder.binding.textViewDateItemNews.text = news.date
        Glide
            .with(holder.binding.imageViewNewsImage)
            .load(news.imageUrl)
            .placeholder(R.drawable.noimage)
            .into(holder.binding.imageViewNewsImage)
        holder.itemView.setOnClickListener {
            onNewsClick?.invoke(news)
        }
    }

}