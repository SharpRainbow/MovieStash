package ru.mirea.moviestash.news

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.ItemNewsBinding
import ru.mirea.moviestash.entites.News

class NewsAdapter(private val news: List<News>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private lateinit var context: Context

    class NewsViewHolder(newsView: View) : RecyclerView.ViewHolder(newsView) {
        lateinit var binding: ItemNewsBinding

        init {
            DataBindingUtil.bind<ItemNewsBinding>(newsView)?.let {
                binding = it
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        context = parent.context
        val item = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(item)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = news[position]
        holder.binding.news = item
        if (item.bmp != null) holder.binding.newsImage.setImageBitmap(item.bmp)
        else holder.binding.newsImage.setImageResource(R.drawable.noimage)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, NewsActivity::class.java).putExtra("NEW", item))
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }

}