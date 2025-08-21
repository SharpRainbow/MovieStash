package ru.mirea.moviestash.presentation.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.databinding.ItemReviewBinding
import ru.mirea.moviestash.domain.entities.ReviewEntity
import ru.mirea.moviestash.presentation.review_list.ReviewDiffCallback

class ReviewAdapter :
    ListAdapter<ReviewEntity, ReviewAdapter.ReviewViewHolder>(
        ReviewDiffCallback()
    ) {

    var onReviewClick: ((ReviewEntity) -> Unit)? = null

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
        getItem(position)?.let { review ->
            with(holder.binding) {
                textViewReviewDate.text = review.date
                textViewUserNickname.text = review.userName
                textViewReviewTitle.text = review.title
                textViewReviewDescription.text = review.description
            }
            holder.itemView.setOnClickListener {
                onReviewClick?.invoke(review)
            }
        }
    }

}