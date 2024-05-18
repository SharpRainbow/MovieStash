package ru.mirea.moviestash.collections

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemUserColBinding

class UserCollectionAdapter(
    private val usrCols: List<ru.mirea.moviestash.entites.Collection>,
    private val clickListener: (colId: Int) -> Unit
) : RecyclerView.Adapter<UserCollectionAdapter.UsrColsViewHolder>() {

    private lateinit var context: Context

    class UsrColsViewHolder(val binding: ItemUserColBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsrColsViewHolder {
        context = parent.context
        val binding = ItemUserColBinding.inflate(LayoutInflater.from(context), parent, false)
        return UsrColsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsrColsViewHolder, position: Int) {
        val col = usrCols[position]
        holder.binding.userColName.text = col.name
        holder.binding.userColDesc.text = col.description
        holder.itemView.setOnClickListener {
            clickListener(col.id)
        }
    }

    override fun getItemCount(): Int {
        return usrCols.size
    }


}