package ru.mirea.moviestash.collections

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemUserColBinding

class UserCollectionAdapter(private val usrCols: List<Collection>,
                            private val clickListener: (colId: Int) -> Unit):
    RecyclerView.Adapter<UserCollectionAdapter.UsrColsViewHolder>() {

    private lateinit var context: Context

    class UsrColsViewHolder(colView: View): RecyclerView.ViewHolder(colView){
        lateinit var binding: ItemUserColBinding
        init {
            DataBindingUtil.bind<ItemUserColBinding>(colView)?.let {
                binding = it
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsrColsViewHolder {
        context = parent.context
        val binding = ItemUserColBinding.inflate(LayoutInflater.from(context), parent, false)
        return UsrColsViewHolder(binding.root)
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