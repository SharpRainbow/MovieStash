package ru.mirea.moviestash.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.celebrities.PersonActivity
import ru.mirea.moviestash.databinding.ItemSearchedBinding
import ru.mirea.moviestash.entites.Celebrity

class SearchPersonAdapter(private val persons: List<Celebrity>) :
    RecyclerView.Adapter<SearchMovieAdapter.SearchViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SearchMovieAdapter.SearchViewHolder {
        context = parent.context
        val binding = ItemSearchedBinding.inflate(LayoutInflater.from(context), parent, false)
        return SearchMovieAdapter.SearchViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SearchMovieAdapter.SearchViewHolder, position: Int) {
        val pItem = persons[position]
        holder.binding.searchName.text = pItem.name
        if (pItem.description.isNotEmpty()) {
            holder.binding.searchDesc.text = pItem.description
        }
        pItem.bmp?.let {
            holder.binding.searchImage.setImageBitmap(it)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PersonActivity::class.java)
            intent.putExtra("PERSON", pItem)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return persons.size
    }
}