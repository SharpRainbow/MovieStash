package ru.mirea.moviestash.content

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemContentBinding
import ru.mirea.moviestash.entites.Content

class ContentAdapter(
    private val contents: List<Content>, private val clickListener: (pos: Int) -> Unit
) : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    private lateinit var context: Context

    class ContentViewHolder(contentView: View) : RecyclerView.ViewHolder(contentView) {
        lateinit var binding: ItemContentBinding

        init {
            DataBindingUtil.bind<ItemContentBinding>(contentView)?.let {
                binding = it
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        context = parent.context
        val binding = ItemContentBinding.inflate(LayoutInflater.from(context), parent, false)
        return ContentViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val contItem = contents[position]
        holder.binding.content = contItem
        contItem.bmp?.let {
            holder.binding.imageView.setImageBitmap(it)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ContentActivity::class.java)
            intent.putExtra("CONTENT", contents[position])
            context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            clickListener(contItem.id)
            true
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}