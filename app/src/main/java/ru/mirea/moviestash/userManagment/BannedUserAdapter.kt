package ru.mirea.moviestash.userManagment

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.collections.UserCollectionAdapter
import ru.mirea.moviestash.Utils
import ru.mirea.moviestash.databinding.ItemUserColBinding

class BannedUserAdapter(private val banUsers: List<BannedUser>,
                        private val clickListener: (id: Int) -> Unit):
    RecyclerView.Adapter<UserCollectionAdapter.UsrColsViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserCollectionAdapter.UsrColsViewHolder {
        context = parent.context
        val binding = ItemUserColBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserCollectionAdapter.UsrColsViewHolder(binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserCollectionAdapter.UsrColsViewHolder, position: Int) {
        val usr = banUsers[position]
        holder.binding.userColDesc.isSingleLine = false
        holder.binding.userColName.text = "${usr.nickname} ${usr.email}"
        holder.binding.userColDesc.text = "Заблокирован ${Utils.dateToString(usr.date)}" +
                " по причине: ${usr.reason ?: ""}"
        holder.itemView.setOnClickListener {
            clickListener(usr.uid)
        }
    }

    override fun getItemCount(): Int {
        return banUsers.size
    }

}