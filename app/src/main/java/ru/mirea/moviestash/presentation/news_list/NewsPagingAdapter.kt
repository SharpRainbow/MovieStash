package ru.mirea.moviestash.presentation.news_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.ItemNewsBinding
import ru.mirea.moviestash.domain.entities.NewsEntity

class NewsPagingAdapter : PagingDataAdapter<NewsEntity, NewsPagingAdapter.NewsViewHolder>
    (
    NewsDiffCallback()
) {

    var onNewsClick: ((NewsEntity) -> Unit)? = null

    class NewsViewHolder(
        val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let { news ->
            with(holder.binding) {
                textViewTitleItemNews.text = news.title
                textViewDateItemNews.text = news.date
                Glide
                    .with(imageViewNewsImage)
                    .load(news.imageUrl)
                    .placeholder(R.drawable.noimage)
                    .into(imageViewNewsImage)
            }
            holder.itemView.setOnClickListener {
                onNewsClick?.invoke(news)
            }
        }
    }

}