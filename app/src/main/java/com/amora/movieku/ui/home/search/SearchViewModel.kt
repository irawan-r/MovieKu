package com.amora.movieku.ui.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.network.Movie.Companion.toMovie
import com.amora.movieku.data.model.network.MovieResponse
import com.amora.movieku.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
	private val repository: MainRepository
) : ViewModel() {

	private val _moviesState = MutableStateFlow<State<MovieResponse>>(State.Empty())
	val moviesState = _moviesState.asStateFlow()

	fun resetState() {
		_moviesState.update {
			State.Empty()
		}
	}

	fun searchMovies(keywords: String?) {
		viewModelScope.launch {
			repository.searchMovies(keywords, onSuccess = { data ->
				_moviesState.update { State.Success(data) }
			}, onError = { msg ->
				_moviesState.update { State.Error(msg) }
			}).onStart { _moviesState.update { State.Loading() } }
				.onEmpty { _moviesState.update { State.Loading() } }
				.catch {error ->
					_moviesState.update { State.Error(error.localizedMessage) }
				}
				.collect()
		}
	}

}