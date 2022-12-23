package ru.mirea.moviestash.celebrities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemPersonBinding

class CelebrityAdapter(private val celebrities: List<CelebrityInContent>) :
    RecyclerView.Adapter<CelebrityAdapter.CelebrityViewHolder>(){

    private lateinit var context: Context

    class CelebrityViewHolder(celView: View) : RecyclerView.ViewHolder(celView){
        lateinit var binding: ItemPersonBinding
        init {
            DataBindingUtil.bind<ItemPersonBinding>(celView)?.let {
                binding = it
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CelebrityViewHolder {
        context = parent.context
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(context), parent, false)
        return CelebrityViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CelebrityViewHolder, position: Int) {
        val person = celebrities[position]
        holder.binding.person = person
        celebrities[position].imageBitmap?.let {
            holder.binding.personImage.setImageBitmap(it)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PersonActivity::class.java)
            intent.putExtra("PERSON", Celebrity(person.id, person.name))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return celebrities.size
    }
}