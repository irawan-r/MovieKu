package com.amora.movieku.ui.home.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amora.movieku.MainActivity
import com.amora.movieku.R
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.databinding.FragmentUpcomingBinding
import com.amora.movieku.ui.adapter.LoadingStateAdapter
import com.amora.movieku.ui.adapter.PagingMoviesAdapter
import com.amora.movieku.ui.base.BaseFragment
import com.amora.movieku.ui.detail.DetailViewModel
import com.amora.movieku.utils.showSnackbarNotice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpcomingFragment : BaseFragment<FragmentUpcomingBinding, UpcomingViewModel>(),
	PagingMoviesAdapter.OnItemClickListener {
	override val inflateBinding: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUpcomingBinding
		get() = FragmentUpcomingBinding::inflate
	override val viewModel: UpcomingViewModel by viewModels()
	private lateinit var adapterMovies: PagingMoviesAdapter

	override fun initView() {
		adapterMovies = PagingMoviesAdapter(requireContext())
		adapterMovies.setOnItemClickListener(this)
		binding?.apply {
			rvUpcomingMovies.adapter = adapterMovies
			val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
			rvUpcomingMovies.layoutManager = layoutManager
			rvUpcomingMovies.adapter = adapterMovies.withLoadStateFooter(
				footer = LoadingStateAdapter { adapterMovies.retry() }
			)
			swipeRefresh.setOnRefreshListener {
				viewModel.getUpcomingMovies()
			}
		}
		(requireActivity() as MainActivity).setUpcomingFragment(this)
	}

	private fun loadingState(toggle: Boolean) {
		binding?.apply {
			progressBar.isVisible = toggle
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
				viewModel.getUpcomingMovies()
				binding?.rvUpcomingMovies?.scrollToPosition(0)
				launch {
					adapterMovies.loadStateFlow.onEach { loadStates ->
						when {
							// Loading state
							loadStates.mediator?.refresh is LoadState.Loading -> {
								// Handle loading state
								// You can show a loading indicator, for example
								loadingState(true)
							}
							// Finished state
							loadStates.mediator?.refresh is LoadState.NotLoading -> {
								// Handle finished state
								// You can hide the loading indicator or perform any other actions
								loadingState(false)
							}
							// Error state
							loadStates.mediator?.refresh is LoadState.Error -> {
								// Handle error state
								// You can show an error message or perform error-related actions
								loadingState(false)
							}
						}
					}.launchIn(lifecycleScope)
				}

				launch {
					viewModel.moviesState.onEach { state ->
						when (state) {
							is State.Loading -> {
								loadingState(true)
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
								binding?.swipeRefresh?.isRefreshing = false
							}

							is State.Success -> {
								loadingState(false)
								binding?.swipeRefresh?.isRefreshing = false
								adapterMovies.submitData(lifecycle, PagingData.empty())
								adapterMovies.submitData(
									lifecycle,
									state.data ?: PagingData.empty()
								)
							}

							else -> {
								loadingState(false)
							}
						}
					}.onCompletion {
						viewModel.resetState()
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