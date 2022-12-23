package ru.mirea.moviestash.reviews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemReviewBinding

class ReviewAdapter(private val review: List<Review>): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>(){

    private lateinit var context: Context

    class ReviewViewHolder(revView: View): RecyclerView.ViewHolder(revView){
        lateinit var binding: ItemReviewBinding
        init {
            DataBindingUtil.bind<ItemReviewBinding>(revView)?.let {
                binding = it
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        context = parent.context
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReviewViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = review[position]
        holder.binding.review = review
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ReviewActivity::class.java)
            intent.putExtra("REV", review)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return review.size
    }


}