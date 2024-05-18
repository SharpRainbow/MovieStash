package ru.mirea.moviestash.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.Utils
import ru.mirea.moviestash.content.ContentActivity
import ru.mirea.moviestash.databinding.ItemSearchedBinding

class SearchMovieAdapter(private val contents: List<ru.mirea.moviestash.entites.Content>) :
    RecyclerView.Adapter<SearchMovieAdapter.SearchViewHolder>() {

    private lateinit var context: Context

    class SearchViewHolder(searchView: View) : RecyclerView.ViewHolder(searchView) {
        lateinit var binding: ItemSearchedBinding

        init {
            DataBindingUtil.bind<ItemSearchedBinding>(searchView)?.let {
                binding = it
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        context = parent.context
        val binding = ItemSearchedBinding.inflate(LayoutInflater.from(context), parent, false)
        return SearchViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val contItem = contents[position]
        holder.binding.searchName.text = contItem.name
        holder.binding.searchDesc.text = Utils.dateToString(contItem.released)
        contItem.bmp?.let {
            holder.binding.searchImage.setImageBitmap(it)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ContentActivity::class.java)
            intent.putExtra("CONTENT", contents[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}