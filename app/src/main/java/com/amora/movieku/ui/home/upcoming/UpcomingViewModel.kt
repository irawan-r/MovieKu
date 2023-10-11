package com.amora.movieku.ui.home.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.network.Movie.Companion.toMovie
import com.amora.movieku.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
	private val repository: MainRepository
) : ViewModel() {

	private val _moviesState = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
	val moviesState = _moviesState.asStateFlow()

	private val _adapterState: MutableStateFlow<CombinedLoadStates?> = MutableStateFlow(null)
	val adapterState = _adapterState.asStateFlow()

	fun updateAdapterState(state: CombinedLoadStates?) {
		_adapterState.update { state }
	}

	fun getUpcomingMovies() {
		viewModelScope.launch {
			repository.getUpcomingMovies().cachedIn(viewModelScope)
				.collect { response ->
					_moviesState.update {
						val data = response.map { it.toMovie() }
						data
					}
				}
		}
	}
}