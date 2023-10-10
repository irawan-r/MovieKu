package com.amora.movieku.ui.home.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amora.movieku.MainActivity
import com.amora.movieku.R
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.databinding.FragmentPopularBinding
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
class PopularFragment : BaseFragment<FragmentPopularBinding, PopularViewModel>(),
	PagingMoviesAdapter.OnItemClickListener {
	override val inflateBinding: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPopularBinding
		get() = FragmentPopularBinding::inflate
	override val viewModel: PopularViewModel by viewModels()
	private lateinit var adapterMovies: PagingMoviesAdapter

	override fun initView() {
		adapterMovies = PagingMoviesAdapter(requireContext())
		adapterMovies.setOnItemClickListener(this)
		binding?.apply {
			rvPopularMovies.adapter = adapterMovies
			val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
			rvPopularMovies.layoutManager = layoutManager
			rvPopularMovies.adapter = adapterMovies.withLoadStateFooter(
				footer = LoadingStateAdapter { adapterMovies.retry() }
			)
			swipeRefresh.setOnRefreshListener {
				viewModel.getMoviesPopular()
			}
		}
		(requireActivity() as MainActivity).setPopularFragment(this)
	}

	override fun initObserver() {
		lifecycleScope.launch {

			launch {
				repeatOnLifecycle(Lifecycle.State.RESUMED) {
					viewModel.getMoviesPopular()
					binding?.rvPopularMovies?.scrollToPosition(0)
				}
			}

			repeatOnLifecycle(Lifecycle.State.CREATED) {
				viewModel.getMoviesPopular()
				binding?.rvPopularMovies?.scrollToPosition(0)
				launch {
					adapterMovies.addLoadStateListener { loadState ->
						// Only show the list if refresh succeeds.
						binding?.rvPopularMovies?.isVisible = loadState.source.refresh is LoadState.NotLoading
						// Show loading spinner during initial load or refresh.
						loadingState(loadState.source.refresh is LoadState.Loading)
						// Show the retry state if initial load or refresh fails.
						//binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

						// Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
						val errorState = loadState.source.append as? LoadState.Error
							?: loadState.source.prepend as? LoadState.Error
							?: loadState.append as? LoadState.Error
							?: loadState.prepend as? LoadState.Error
						errorState?.let {
							Toast.makeText(
								context,
								"\uD83D\uDE28 Wooops ${it.error}",
								Toast.LENGTH_LONG
							).show()
						}
					}
				}

				launch {
					viewModel.moviesState.onEach { state ->
							when (state) {
								is State.Loading -> {

								}

								is State.Error -> {

									binding?.swipeRefresh?.isRefreshing = false
									val messageApi = state.data.toString()
									val message = state.message
									if (message != null) {
										binding?.root?.showSnackbarNotice(message)
									} else {
										binding?.root?.showSnackbarNotice(messageApi)
									}
								}

								is State.Success -> {
									binding?.swipeRefresh?.isRefreshing = false
									adapterMovies.submitData(
										lifecycle,
										state.data ?: PagingData.empty()
									)
								}

								else -> {

								}
							}
						}
						.onCompletion {
							viewModel.resetState()
						}.collect()
				}
			}
		}
	}

	private fun loadingState(toggle: Boolean) {
		binding?.apply {
			progressBar.isVisible = toggle
		}
	}

	override fun onItemClick(item: Movie) {
		val idMovie = item.id
		val args = Bundle()
		args.putLong(DetailViewModel.ID_MOVIE, idMovie)
		findNavController().navigate(R.id.navigation_detail, args)
	}


}