package ru.mirea.moviestash.user_management

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemUserColBinding
import ru.mirea.moviestash.entites.Credentials

class CredentialsAdapter(
    private val credentials: MutableList<Credentials>,
    private val clickListener: (credentials: Credentials) -> Unit,
    private val deleteAction: (credentials: Credentials) -> Unit
) : RecyclerView.Adapter<CredentialsAdapter.CredsViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CredsViewHolder {
        context = parent.context
        val binding = ItemUserColBinding.inflate(LayoutInflater.from(context), parent, false)
        return CredsViewHolder(binding)
    }

    inner class CredsViewHolder(val binding: ItemUserColBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        private val onMenuClick = MenuItem.OnMenuItemClickListener {
            val pos = this@CredsViewHolder.adapterPosition
            if (it.itemId == 1) {
                val item = credentials[pos]
                credentials.removeAt(pos)
                notifyItemRemoved(pos)
                deleteAction(item)
            }
            return@OnMenuItemClickListener true
        }

        override fun onCreateContextMenu(
            contextMenu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            contextMenu?.let { menu ->
                val del: MenuItem = menu.add(Menu.NONE, 1, Menu.NONE, "Удалить")
                del.setOnMenuItemClickListener(onMenuClick)
            }
        }

    }

    override fun onBindViewHolder(holder: CredsViewHolder, position: Int) {
        val cred = credentials[position]
        holder.binding.userColName.text = cred.username
        holder.binding.userColDesc.text = cred.email
        holder.itemView.setOnCreateContextMenuListener(holder)
        holder.itemView.setOnClickListener {
            clickListener(cred)
        }
    }

    override fun getItemCount(): Int {
        return credentials.size
    }

}