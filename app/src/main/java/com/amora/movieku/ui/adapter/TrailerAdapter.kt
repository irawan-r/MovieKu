package com.amora.movieku.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amora.movieku.BuildConfig
import com.amora.movieku.data.model.network.VideoMovie
import com.amora.movieku.databinding.AnimeTrailerItemBinding
import com.bumptech.glide.Glide

class TrailerAdapter(private val clickListener: TrailerListener, private val context: Context) :
    ListAdapter<VideoMovie, TrailerAdapter.TrailerViewHolder>(TrailerDiffCallback) {

    object TrailerDiffCallback : DiffUtil.ItemCallback<VideoMovie>() {
        override fun areItemsTheSame(oldItem: VideoMovie, newItem: VideoMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoMovie, newItem: VideoMovie): Boolean {
            return oldItem == newItem
        }
    }

    class TrailerViewHolder(
        var binding: AnimeTrailerItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: TrailerListener, data: VideoMovie) {
            binding.root.setOnClickListener {
                data.key?.let { it1 -> clickListener.onClick(it1) }
            }
        }
    }

    class TrailerListener(val clickListener: (url: String) -> Unit) {
        fun onClick(url: String) = clickListener(url)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrailerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TrailerViewHolder(
            AnimeTrailerItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        val trailer = getItem(position)
        holder.bind(clickListener, trailer)
        holder.binding.apply {
            Glide.with(context).load(getYouTubeThumbnailUrl(trailer.key)).into(posterTrailer)
        }
    }

    private fun getYouTubeThumbnailUrl(videoKey: String?, quality: String = "default"): String {
        return "https://img.youtube.com/vi/$videoKey/$quality.jpg"
    }
}