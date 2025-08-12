package ru.mirea.moviestash.presentation.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemReviewBinding
import ru.mirea.moviestash.domain.entities.ReviewEntity

class ReviewAdapter :
    ListAdapter<ReviewEntity, ReviewAdapter.ReviewViewHolder>(
        ReviewDiffCallback()
    ) {

    var onReviewClick: ((ReviewEntity) -> Unit)? = null
    var onReachEnd: (() -> Unit)? = null

    class ReviewViewHolder(
        val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = getItem(position)
        holder.binding.textViewReviewDate.text = review.date
        holder.binding.textViewUserNickname.text = review.userName
        holder.binding.textViewReviewTitle.text = review.title
        holder.binding.textViewReviewDescription.text = review.description
        holder.itemView.setOnClickListener {
            onReviewClick?.invoke(review)
        }
        if (position >= itemCount - 5) {
            onReachEnd?.invoke()
        }
    }

}