package com.amora.movieku.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.MovieDetail
import com.amora.movieku.data.model.network.MovieVideoResponse
import com.amora.movieku.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val repository: MainRepository
) : ViewModel() {

	private val _moviesState = MutableStateFlow<State<MovieDetail>>(State.Empty())
	val moviesState = _moviesState.asStateFlow()

	private val _moviesVideo = MutableStateFlow<State<MovieVideoResponse>>(State.Empty())
	val moviesVideo = _moviesVideo.asStateFlow()

	fun resetVideoState() {
		_moviesVideo.update {
			State.Empty()
		}
	}

	fun resetState() {
		_moviesState.update {
			State.Empty()
		}
	}

	init {
		getMoviesPopular()
		getMoviesVideo()
	}

	companion object {
		const val ID_MOVIE = "id_movie"
	}

	private fun getMoviesVideo() {
		viewModelScope.launch {
			val idDetail = savedStateHandle.get<Long>(ID_MOVIE) ?: 0L
			println(idDetail)
			repository.movieVideo(idDetail, onSuccess = { data ->
				_moviesVideo.update { State.Success(data) }
			}) { msg ->
				_moviesVideo.update { State.Error(msg) }
			}
				.onStart { _moviesVideo.update { State.Loading() } }
				.collect()
		}
	}

	private fun getMoviesPopular() {
		viewModelScope.launch {
			val idDetail = savedStateHandle.get<Long>(ID_MOVIE) ?: 0L
			println(idDetail)
			repository.movieDetail(idDetail, onSuccess = { data ->
				_moviesState.update { State.Success(data) }
			}, onError = { msg ->
				_moviesState.update { State.Error(msg) }
			})
				.onStart { _moviesState.update { State.Loading() } }
				.collect()
		}
	}

}