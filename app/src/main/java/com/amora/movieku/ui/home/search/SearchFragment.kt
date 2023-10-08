package com.amora.movieku.ui.home.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amora.movieku.R
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.databinding.FragmentSearchBinding
import com.amora.movieku.ui.adapter.MoviesAdapter
import com.amora.movieku.ui.base.BaseFragment
import com.amora.movieku.ui.detail.DetailViewModel
import com.amora.movieku.utils.showSnackbarNotice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(),
	MoviesAdapter.OnItemClickListener {
	override val inflateBinding: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
		get() = FragmentSearchBinding::inflate
	override val viewModel: SearchViewModel by viewModels()
	private lateinit var adapterMovies: MoviesAdapter

	override fun initView() {
		binding?.apply {
			adapterMovies = MoviesAdapter(requireContext())
			adapterMovies.setOnItemClickListener(this@SearchFragment)
			rvSearchMovies.adapter = adapterMovies
			val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
			rvSearchMovies.layoutManager = layoutManager

			btSearch.setOnClickListener {
				viewModel.searchMovies(etSearch.text.toString())
			}
		}
	}

	private fun loadingState(toggle: Boolean) {
		binding?.apply {
			progressBar.isVisible = toggle
		}
	}

	private fun toggleInfoInitial(isVisible: Boolean) {
		binding?.apply {
			tvInfoInitial.isVisible = isVisible
		}
	}

	private fun toggleInfoEmpty(isVisible: Boolean) {
		binding?.apply {
			tvInfoEmpty.isVisible = isVisible
		}
	}

	override fun onResume() {
		super.onResume()
		if (adapterMovies.itemCount != 0) {
			binding?.tvInfoInitial?.isVisible = false
		}
	}

	override fun initObserver() {
		lifecycleScope.launch {
			viewModel.moviesState.onEach { state ->
				when (state) {
					is State.Loading -> {
						loadingState(true)
						toggleInfoEmpty(false)
						toggleInfoInitial(false)
					}

					is State.Error -> {
						loadingState(false)
						toggleInfoEmpty(true)
						toggleInfoInitial(false)
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
						val movies = state.data?.results ?: emptyList()
						if (movies.isEmpty()) {
							toggleInfoEmpty(true)
						} else {
							toggleInfoEmpty(false)
						}
						adapterMovies.submitList(movies)
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

	override fun onItemClick(item: Movie) {
		val idMovie = item.id
		val args = Bundle()
		args.putLong(DetailViewModel.ID_MOVIE, idMovie)
		findNavController().navigate(R.id.navigation_detail, args)
	}
}
