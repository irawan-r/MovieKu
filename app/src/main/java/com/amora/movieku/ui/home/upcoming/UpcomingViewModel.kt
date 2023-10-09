package com.amora.movieku.ui.home.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

	private val _moviesState = MutableStateFlow<State<PagingData<Movie>>>(State.Empty())
	val moviesState = _moviesState.asStateFlow()

	fun resetState() {
		_moviesState.update {
			State.Empty()
		}
	}

	init {
		getUpcomingMovies()
	}

	fun getUpcomingMovies() {
		viewModelScope.launch {
			repository.getUpcomingMovies().cachedIn(viewModelScope)
				.onStart { _moviesState.update { State.Loading() } }
				.onEmpty { _moviesState.update { State.Loading() } }
				.collect { response ->
					_moviesState.update {
						val data = response.map { it.toMovie() }
						State.Success(data)
					}
				}
		}
	}
}