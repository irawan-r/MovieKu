package com.amora.movieku.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amora.movieku.BuildConfig
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.databinding.MoviesPosterBinding
import com.bumptech.glide.Glide

class PagingMoviesAdapter(private val context: Context) :
	PagingDataAdapter<Movie, RecyclerView.ViewHolder>(differCallback) {

	inner class ItemStoryVH(val binding: MoviesPosterBinding) :
		RecyclerView.ViewHolder(binding.root)

	companion object {

		val differCallback = object : DiffUtil.ItemCallback<Movie>() {
			override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
				return oldItem.id == newItem.id
			}

			override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
				return oldItem == newItem
			}
		}
	}

	interface OnItemClickListener {
		fun onItemClick(item: Movie)
	}

	private var onItemClickListener: OnItemClickListener? = null

	fun setOnItemClickListener(listener: OnItemClickListener) {
		onItemClickListener = listener
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val itemHolder = holder as ItemStoryVH
		val item = getItem(position)
		itemHolder.itemView.setOnClickListener {
			item?.let { onItemClickListener?.onItemClick(it) }
		}
		itemHolder.binding.apply {
			scoreValue.text = item?.popularity.toString()
			val imgUrl = "${BuildConfig.IMG_URL_PV}${item?.poster_path}"
			Glide.with(context).load(imgUrl).into(poster)
			titleMovies.text = item?.title
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val binding = MoviesPosterBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return ItemStoryVH(binding)
	}
}