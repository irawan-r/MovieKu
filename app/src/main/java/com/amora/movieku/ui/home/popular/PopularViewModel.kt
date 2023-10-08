package com.amora.movieku.ui.home.popular

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
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
		getMoviesPopular()
	}

	fun getMoviesPopular() {
		viewModelScope.launch {
			repository.getPopularMovies(onError = { msg ->
				_moviesState.update { State.Error(msg) }
			}).cachedIn(viewModelScope)
				.onStart { _moviesState.update { State.Loading() } }
				.onEmpty { _moviesState.update { State.Loading() } }
				.catch { exception ->
					if (exception is IOException) {
						val message = exception.message
						if (message != null) {
							_moviesState.update { State.Error(message) }
						}
					} else {
						exception.message?.let { msg -> _moviesState.update { State.Error(message = msg) } }
					}
				}
				.collect { response ->
					_moviesState.update {
						val data = response.map { it.toMovie() }
						State.Success(data)
					}
				}
		}
	}
}