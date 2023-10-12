package com.amora.movieku.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amora.movieku.data.State
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.network.MovieDetail
import com.amora.movieku.data.model.network.MovieReviewsResponse
import com.amora.movieku.data.model.network.MovieVideoResponse
import com.amora.movieku.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
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

	private val _moviesReviews = MutableStateFlow<State<MovieReviewsResponse>>(State.Empty())
	val moviesReviews = _moviesReviews.asStateFlow()

	private val movie= MutableStateFlow<Movie?>(null)

	private val _notifyUpdate = Channel<String>()
	val notifyUpdate = _notifyUpdate.receiveAsFlow()

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
		getDetailMovie()
		getMoviesVideo()
		getMoviesReviews()
	}

	companion object {
		const val ID_MOVIE = "id_movie"
	}

	fun addToFavorite() {
		viewModelScope.launch {
			movie.collectLatest {
				if (it != null) {
					val dataExist = repository.getFavoriteMovie(it.id)
					if (dataExist != null) {
						_notifyUpdate.trySend("Favorite deleted")
						repository.deleteFavoriteMovie(it)
					} else {
						_notifyUpdate.trySend("Favorite added")
						repository.insertFavoriteMovie(it)
					}
				}
			}
		}
	}

	private fun getMoviesVideo() {
		viewModelScope.launch {
			val idDetail = savedStateHandle.get<Long>(ID_MOVIE) ?: 0L
			repository.movieVideo(idDetail, onSuccess = { data ->
				_moviesVideo.update {
					State.Success(data)
				}
			}) { msg ->
				_moviesVideo.update { State.Error(msg) }
			}
				.onStart { _moviesVideo.update { State.Loading() } }
				.collect()
		}
	}

	private fun getMoviesReviews() {
		viewModelScope.launch {
			val idDetail = savedStateHandle.get<Long>(ID_MOVIE) ?: 0L
			repository.movieReviews(idDetail, onSuccess = { data ->
				_moviesReviews.update { State.Success(data) }
			}) { msg ->
				_moviesReviews.update { State.Error(msg) }
			}
				.onStart { _moviesReviews.update { State.Loading() } }
				.collect()
		}
	}

	private fun getDetailMovie() {
		viewModelScope.launch {
			val idDetail = savedStateHandle.get<Long>(ID_MOVIE) ?: 0L
			repository.movieDetail(idDetail, onSuccess = { data ->
				movie.update {
					Movie(
						overview = data.overview,
						title = data.title,
						poster_path = data.poster_path,
						releaseDate = data.release_date,
						popularity = data.popularity,
						voteAverage = data.vote_average?.toInt(),
						id = data.id ?: 0L,
						vote_count =  data.vote_count
					)
				}
				_moviesState.update { State.Success(data) }
			}, onError = { msg ->
				_moviesState.update { State.Error(msg) }
			})
				.onStart { _moviesState.update { State.Loading() } }
				.collect()
		}
	}

}