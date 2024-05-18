package ru.mirea.moviestash.user_management

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.Utils
import ru.mirea.moviestash.collections.UserCollectionAdapter
import ru.mirea.moviestash.databinding.ItemUserColBinding
import ru.mirea.moviestash.entites.SiteUser

class UserAdapter(
    private val banUsers: List<SiteUser>, private val clickListener: (id: Int) -> Unit
) : RecyclerView.Adapter<UserCollectionAdapter.UsrColsViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): UserCollectionAdapter.UsrColsViewHolder {
        context = parent.context
        val binding = ItemUserColBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserCollectionAdapter.UsrColsViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserCollectionAdapter.UsrColsViewHolder, position: Int) {
        val usr = banUsers[position]
        holder.binding.userColDesc.isSingleLine = false
        holder.binding.userColName.text = "${usr.nickname} ${usr.email}"
        holder.binding.userColDesc.text =
            "Заблокирован ${Utils.dateToString(usr.banDate)}" + " по причине: ${usr.banReason ?: ""}"
        holder.itemView.setOnClickListener {
            clickListener(usr.id)
        }
    }

    override fun getItemCount(): Int {
        return banUsers.size
    }

}