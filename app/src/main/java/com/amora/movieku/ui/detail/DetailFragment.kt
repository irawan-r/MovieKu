package com.amora.movieku.ui.detail

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.amora.movieku.BuildConfig
import com.amora.movieku.R
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.MovieDetail
import com.amora.movieku.databinding.FragmentDetailBinding
import com.amora.movieku.ui.adapter.ReviewAdapter
import com.amora.movieku.ui.adapter.TrailerAdapter
import com.amora.movieku.ui.base.BaseFragment
import com.amora.movieku.utils.DateUtils.formatDate
import com.amora.movieku.utils.showSnackbarNotice
import com.amora.movieku.utils.toast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding, DetailViewModel>() {
	override val inflateBinding: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailBinding
		get() = FragmentDetailBinding::inflate
	override val viewModel: DetailViewModel by viewModels()
	private lateinit var adapter: TrailerAdapter
	private lateinit var reviewsAdapter: ReviewAdapter
	override fun initView() {
		binding?.apply {
			adapter = TrailerAdapter(
				TrailerAdapter.TrailerListener { url -> showTrailer(url) },
				requireContext()
			)
			trailerRv.adapter = adapter

			reviewsAdapter = ReviewAdapter()
			rvReviews.adapter = reviewsAdapter

			scrollMovie.viewTreeObserver.addOnScrollChangedListener {
				val scrollY = scrollMovie.scrollY
				if (scrollY > 0 && fabFavorie.isExtended) {
					fabFavorie.shrink()
				} else if (scrollY == 0 && !fabFavorie.isExtended) {
					fabFavorie.extend()
				}
			}
			fabFavorie.setOnClickListener {
				viewModel.addToFavorite()
			}
		}
	}


	private fun setDetailMovies(data: MovieDetail?) {
		binding?.apply {
			titleMovies.text = data?.title
			moviesTitle.text = data?.original_title
			dateAiring.text = formatDate(data?.release_date)
			val backDropUrl = "${BuildConfig.IMG_URL_ORG}${data?.backdrop_path}"
			Glide.with(requireContext()).load(backDropUrl).into(imageBackground)
			val imgUrl = "${BuildConfig.IMG_URL_ORG}${data?.poster_path}"
			Glide.with(requireContext()).load(imgUrl).into(posterMovie)
			synopsysDescription.text = data?.overview
			ratingValue.text = data?.vote_average.toString()
			popularityValue.text = data?.popularity.toString()
			reviewValue.text = data?.vote_count.toString()
			setGenresText(data)
		}
	}

	private fun setGenresText(data: MovieDetail?) {
		data?.genres?.let {
			for (genres in it.indices) {

				val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT
				)

				params.setMargins(0, 0, 20, 0)
				val genreTextView = TextView(requireContext()).apply {
					setBackgroundResource(R.drawable.bg_genres)
					layoutParams = params
					setTextColor(Color.parseColor("#ffffff"))
					text = data.genres[genres]?.name
				}

				// Set the list of genres while less than Api size and for not make the list double
				binding?.apply {
					if (listGenres.size < data.genres.size) {
						listGenres.addView(genreTextView)
					}
				}
			}
		}
	}

	private fun toggleEmpty(isVisible: Boolean) {
		binding?.apply {
			emptyResponse.isVisible = isVisible
			materialCardView.isVisible = !isVisible
			moviesTitle.isVisible = !isVisible
			titleMovies.isVisible = !isVisible
			moviesTitle.isVisible = !isVisible
			dateAiring.isVisible = !isVisible
			imageBackground.isVisible = !isVisible
			posterMovie.isVisible = !isVisible
			synopsysDescription.isVisible = !isVisible
			ratingValue.isVisible = !isVisible
			popularityValue.isVisible = !isVisible
			reviewValue.isVisible = !isVisible
			listGenres.isVisible = !isVisible

			trailerTitle.isVisible = !isVisible
			genresTitle.isVisible = !isVisible
			synopsys.isVisible = !isVisible
			tvRating.isVisible = !isVisible
			tvPopularity.isVisible = !isVisible
			tvReview.isVisible = !isVisible
			tvTitleReviews.isVisible = !isVisible
			fabFavorie.isVisible = !isVisible
			fabFavorie.isEnabled = !isVisible
		}
	}

	private fun loadingState(toggle: Boolean) {
		binding?.apply {
			progressBar.isVisible = toggle
		}
	}

	override fun initObserver() {
		lifecycleScope.launch {
			launch {
				viewModel.notifyUpdate.collectLatest {
					binding?.root?.showSnackbarNotice(it, null)
				}
			}

			launch {
				viewModel.moviesReviews.onEach { state ->
					when (state) {
						is State.Loading -> {
							loadingState(true)
						}

						is State.Error -> {
							toggleEmpty(true)
							loadingState(false)
							val messageApi = state.data.toString()
							val message = state.message
							if (message != null) {
								binding?.root?.showSnackbarNotice(message)
							} else {
								binding?.root?.showSnackbarNotice(messageApi)
							}
						}

						is State.Success -> {
							toggleEmpty(false)
							loadingState(false)
							val data = state.data
							reviewsAdapter.differ.submitList(data?.results)
						}

						else -> {
							toggleEmpty(false)
							loadingState(false)
						}
					}
				}.onCompletion {
					viewModel.resetVideoState()
					loadingState(false)
				}.collect()
			}

			launch {
				viewModel.moviesVideo.onEach { state ->
					when (state) {
						is State.Loading -> {
							loadingState(true)
						}

						is State.Error -> {
							toggleEmpty(true)
							loadingState(false)
							val messageApi = state.data.toString()
							val message = state.message
							if (message != null) {
								binding?.root?.showSnackbarNotice(message)
							} else {
								binding?.root?.showSnackbarNotice(messageApi)
							}
						}

						is State.Success -> {
							toggleEmpty(false)
							loadingState(false)
							val data = state.data
							adapter.submitList(data?.results)
						}

						else -> {
							toggleEmpty(false)
							loadingState(false)
						}
					}
				}.onCompletion {
					viewModel.resetVideoState()
					loadingState(false)
				}.collect()
			}
			launch {
				viewModel.moviesState.onEach { state ->
					when (state) {
						is State.Loading -> {
							loadingState(true)
							binding?.fabFavorie?.isEnabled = false
						}

						is State.Error -> {
							loadingState(false)
							val messageApi = state.data.toString()
							val message = state.message
							if (message != null) {
								binding?.root?.showSnackbarNotice(message)
							} else {
								binding?.root?.showSnackbarNotice(messageApi)
							}
						}

						is State.Success -> {
							loadingState(false)
							val data = state.data
							setDetailMovies(data)
						}

						else -> {
							loadingState(false)
						}
					}
				}.onCompletion {
					viewModel.resetState()
					loadingState(false)
				}.collect()
			}
		}

	}

	private fun showTrailer(key: String?) {
		val url = "https://www.youtube.com/watch?v=$key"
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		try {
			startActivity(intent)
		} catch (t: Throwable) {
			toast(requireContext(), "Ups, slowly!")
		}
	}
}