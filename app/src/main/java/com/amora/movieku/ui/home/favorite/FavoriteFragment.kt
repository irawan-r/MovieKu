package com.amora.movieku.ui.home.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amora.movieku.MainActivity
import com.amora.movieku.R
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.persistence.MovieFavoriteEntity.Companion.toMovie
import com.amora.movieku.databinding.FragmentUpcomingBinding
import com.amora.movieku.ui.adapter.MoviesAdapter
import com.amora.movieku.ui.base.BaseFragment
import com.amora.movieku.ui.detail.DetailViewModel
import com.amora.movieku.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentUpcomingBinding, FavoriteViewModel>(),
	MoviesAdapter.OnItemClickListener {
	override val inflateBinding: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUpcomingBinding
		get() = FragmentUpcomingBinding::inflate
	override val viewModel: FavoriteViewModel by viewModels()
	private lateinit var adapterMovies: MoviesAdapter

	override fun initView() {
		adapterMovies = MoviesAdapter(requireContext())
		adapterMovies.setOnItemClickListener(this)
		binding?.apply {
			rvUpcomingMovies.adapter = adapterMovies
			val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
			rvUpcomingMovies.layoutManager = layoutManager
			swipeRefresh.setOnRefreshListener {
				viewModel.getFavoriteMovies()
			}
		}
		(requireActivity() as MainActivity).setUpcomingFragment(this)
	}

	private fun loadingState(toggle: Boolean, data: List<Movie>?) {
		binding?.apply {
			swipeRefresh.isRefreshing = toggle
			tvNotFound.isVisible = data.isNullOrEmpty()
		}
	}

	override fun initObserver() {
		lifecycleScope.launch {
			launch {
				repeatOnLifecycle(Lifecycle.State.RESUMED) {
					binding?.rvUpcomingMovies?.scrollToPosition(0)
				}
			}

			repeatOnLifecycle(Lifecycle.State.CREATED) {
				viewModel.getFavoriteMovies()

				launch {
					viewModel.moviesState.onEach { state ->
						when (state) {
							is State.Success -> {
								loadingState(false, state.data?.toMovie())
								adapterMovies.submitList(state.data?.toMovie() ?: emptyList())
							}
							is State.Error -> {
								loadingState(false, state.data?.toMovie())
							}
							is State.Loading -> {
								loadingState(false, state.data?.toMovie())
							}
							else -> {
								loadingState(false, state.data?.toMovie())
							}
						}
					}.collect()
				}
			}
		}

	}

	override fun onItemClick(item: Movie) {
		val idMovie = item.id
		val args = Bundle()
		args.putLong(DetailViewModel.ID_MOVIE, idMovie)
		findNavController().navigate(R.id.navigation_detail, args)
	}

}