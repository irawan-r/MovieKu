package com.amora.movieku.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amora.movieku.data.model.network.ResultsItem
import com.amora.movieku.databinding.MoviesReviewsItemBinding


class ReviewAdapter :
    RecyclerView.Adapter<ReviewAdapter.ItemAdapterReviews>() {

    private val differCallback = object : DiffUtil.ItemCallback<ResultsItem>() {
        override fun areItemsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean {
            return oldItem == newItem
        }
    }

    inner class ItemAdapterReviews(val binding: MoviesReviewsItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ReviewAdapter.ItemAdapterReviews {
        val binding = MoviesReviewsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemAdapterReviews(binding)
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onBindViewHolder(holder: ReviewAdapter.ItemAdapterReviews, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            ratingValue.text = (item.author_details?.rating ?: 0.0).toString()
            tvAuthor.text = item.author
            textView.text = item.content
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}