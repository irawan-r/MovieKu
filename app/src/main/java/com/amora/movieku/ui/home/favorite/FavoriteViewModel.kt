package com.amora.movieku.ui.home.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amora.movieku.data.State
import com.amora.movieku.data.model.persistence.MovieFavoriteEntity
import com.amora.movieku.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
	private val repository: MainRepository
) : ViewModel() {

	private val _moviesState = MutableStateFlow<State<List<MovieFavoriteEntity>>>(State.Empty())
	val moviesState = _moviesState.asStateFlow()

	fun getFavoriteMovies() {
		viewModelScope.launch {
			repository.getFavoriteMovies()
				.onStart { _moviesState.update { State.Loading() } }
				.collect { data ->
					_moviesState.update {
						if (data.isEmpty()) {
							State.Empty()
						} else {
							State.Success(data)
						}
					}
				}
		}
	}
}