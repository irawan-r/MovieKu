package com.amora.movieku.ui.home.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amora.movieku.MainActivity
import com.amora.movieku.R
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.databinding.FragmentPopularBinding
import com.amora.movieku.ui.adapter.LoadingStateAdapter
import com.amora.movieku.ui.adapter.PagingMoviesAdapter
import com.amora.movieku.ui.base.BaseFragment
import com.amora.movieku.ui.detail.DetailViewModel
import com.amora.movieku.utils.Constant.NO_CONNECTION
import com.amora.movieku.utils.isNetworkError
import com.amora.movieku.utils.showSnackbarNotice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
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
					binding?.rvPopularMovies?.scrollToPosition(0)
				}
			}

			repeatOnLifecycle(Lifecycle.State.CREATED) {
				viewModel.getMoviesPopular()
				binding?.rvPopularMovies?.scrollToPosition(0)

				adapterMovies.loadStateFlow.onEach { loadStates ->
						val refreshState = loadStates.mediator?.refresh
						when (refreshState) {
							is LoadState.Loading -> {
								loadingState(true)
							}
							is LoadState.NotLoading -> {
								loadingState(false)
							}
							is LoadState.Error -> {
								loadingState(false)
								handleMediatorErrorState(refreshState)
							}

							else -> {
								loadingState(false)
							}
						}
					}.launchIn(lifecycleScope)


				launch {
					viewModel.moviesState.collect { data ->
						adapterMovies.submitData(lifecycle, data)
					}
				}
			}
		}
	}

	private fun handleMediatorErrorState(errorState: LoadState.Error) {
		// Check for network-related errors
		if (isNetworkError(errorState)) {
			// Show Snackbar for no internet connection
			binding?.root?.showSnackbarNotice(NO_CONNECTION)
		} else {
			// Show Snackbar for other errors
			binding?.root?.showSnackbarNotice(errorState.error.localizedMessage)
		}
	}


	private fun loadingState(toggle: Boolean) {
		binding?.apply {
			swipeRefresh.isRefreshing = toggle
		}
	}

	override fun onItemClick(item: Movie) {
		val idMovie = item.id
		val args = Bundle()
		args.putLong(DetailViewModel.ID_MOVIE, idMovie)
		findNavController().navigate(R.id.navigation_detail, args)
	}


}