package com.amora.movieku.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amora.movieku.BuildConfig
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.databinding.MoviesPosterBinding
import com.bumptech.glide.Glide

class MoviesAdapter(private val context: Context) :
	RecyclerView.Adapter<MoviesAdapter.ItemStoryVH>() {

	private val differ = AsyncListDiffer(this, differCallback)

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

	fun setOnItemClickListener(listener: OnItemClickListener) {
		onItemClickListener = listener
	}

	inner class ItemStoryVH(val binding: MoviesPosterBinding) :
		RecyclerView.ViewHolder(binding.root)

	interface OnItemClickListener {
		fun onItemClick(item: Movie)
	}

	fun submitList(list: List<Movie>) {
		differ.submitList(list)
	}

	private var onItemClickListener: OnItemClickListener? = null


	override fun onBindViewHolder(holder: ItemStoryVH, position: Int) {
		val item = differ.currentList[position]
		holder.itemView.setOnClickListener {
			onItemClickListener?.onItemClick(item)
		}
		holder.binding.apply {
			scoreValue.text = item.popularity.toString()
			val imgUrl = "${BuildConfig.IMG_URL_PV}${item.poster_path}"
			Glide.with(context).load(imgUrl).into(poster)
			titleMovies.text = item.title
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemStoryVH {
		val binding = MoviesPosterBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return ItemStoryVH(binding)
	}

	override fun getItemCount(): Int {
		return differ.currentList.size
	}
}
