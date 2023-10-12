package com.amora.movieku.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.amora.movieku.data.model.network.ErrorResponse
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.network.Movie.Companion.toFavoriteEntity
import com.amora.movieku.data.model.network.MovieDetail
import com.amora.movieku.data.model.network.MovieResponse
import com.amora.movieku.data.model.network.MovieResponse.Companion.toFavoriteEntity
import com.amora.movieku.data.model.network.MovieReviewsResponse
import com.amora.movieku.data.model.network.MovieVideoResponse
import com.amora.movieku.data.model.persistence.MovieFavoriteEntity
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import com.amora.movieku.data.persistence.AppDatabase
import com.amora.movieku.network.ApiService
import com.amora.movieku.data.repository.remotemediator.popular.PopularRemoteMediator
import com.amora.movieku.utils.Constant.NO_CONNECTION
import com.amora.movieku.utils.Constant.UNEXPECTED_ERROR
import com.google.gson.Gson
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
	private val apiService: ApiService,
	private val database: AppDatabase
) : MainRepository {
	@OptIn(ExperimentalPagingApi::class)
	override fun getPopularMovies(): Flow<PagingData<MoviePopularEntity>> =
		flow {
			val pager = Pager(
				config = PagingConfig(pageSize = 10),
				remoteMediator = PopularRemoteMediator(database, apiService, 0),
				pagingSourceFactory = {
					database.movieDao().getMoviesPopularList()
				}
			).flow
			emitAll(pager)
		}

	override fun getFavoriteMovies(): Flow<List<MovieFavoriteEntity>> = flow {
		emit(database.movieDao().getMoviesFavorite().toFavoriteEntity())
	}

	override suspend fun insertFavoriteMovie(movie: Movie) {
		database.movieDao().insertFavoriteMovies(movie = movie.toFavoriteEntity())
	}

	override suspend fun deleteFavoriteMovie(movie: Movie) {
		database.movieDao().deleteFavoriteMovie(movie.id)
	}

	override suspend fun getFavoriteMovie(idMovie: Long): Movie? {
		return database.movieDao().getMovieFavorite(idMovie)
	}

	override fun searchMovies(
		keywords: String?,
		onSuccess: (MovieResponse) -> Unit,
		onError: (String) -> Unit
	): Flow<MovieResponse> = flow {
		val getStory = apiService.searchMovies(keywords)
		getStory.suspendOnSuccess {
			onSuccess(data)
			emit(data)
		}.onException {
			onError(message())
		}.onError {
			val message: String? = try {
				val errorMessageObj = Gson().fromJson(message(), ErrorResponse::class.java)
				errorMessageObj.message?.replace("\"", "")
			} catch (e: Exception) {
				onError(e.message.toString())
				null
			}
			if (message != null) {
				onError(message)
			}
		}.onException {
			if (exception is IOException) {
				// Handle network-related errors
				onError(NO_CONNECTION)
			} else {
				// Handle other types of errors
				onError(UNEXPECTED_ERROR)
			}
		}
	}.flowOn(Dispatchers.IO)

	override fun movieDetail(
		idMovie: Long,
		onSuccess: (MovieDetail) -> Unit,
		onError: (String) -> Unit
	): Flow<MovieDetail> = flow {
		val getStory = apiService.getMovieById(idMovie)
		getStory.suspendOnSuccess {
			emit(data)
			onSuccess(data)
		}.onException {
			onError(message())
		}.onError {
			val message: String? = try {
				val errorMessageObj = Gson().fromJson(message(), ErrorResponse::class.java)
				errorMessageObj.message?.replace("\"", "")
			} catch (e: Exception) {
				onError(e.message.toString())
				null
			}
			if (message != null) {
				onError(message)
			}
		}.onException {
			if (exception is IOException) {
				// Handle network-related errors
				onError(NO_CONNECTION)
			} else {
				// Handle other types of errors
				onError(UNEXPECTED_ERROR)
			}
		}
	}.flowOn(Dispatchers.IO)

	override fun movieVideo(
		idMovie: Long,
		onSuccess: (MovieVideoResponse) -> Unit,
		onError: (String) -> Unit
	): Flow<MovieVideoResponse> = flow {
		val getStory = apiService.getMovieVideos(idMovie)
		getStory.suspendOnSuccess {
			emit(data)
			onSuccess(data)
		}.onException {
			onError(message())
		}.onError {
			val message: String? = try {
				val errorMessageObj = Gson().fromJson(message(), ErrorResponse::class.java)
				errorMessageObj.message?.replace("\"", "")
			} catch (e: Exception) {
				onError(e.message.toString())
				null
			}
			if (message != null) {
				onError(message)
			}
		}.onException {
			if (exception is IOException) {
				// Handle network-related errors
				onError(NO_CONNECTION)
			} else {
				// Handle other types of errors
				onError(UNEXPECTED_ERROR)
			}
		}
	}.flowOn(Dispatchers.IO)

	override fun movieReviews(
		idMovie: Long,
		onSuccess: (MovieReviewsResponse) -> Unit,
		onError: (String) -> Unit
	): Flow<MovieReviewsResponse> = flow {
		val getStory = apiService.getMovieReviews(idMovie)
		getStory.suspendOnSuccess {
			emit(data)
			onSuccess(data)
		}.onException {
			onError(message())
		}.onError {
			val message: String? = try {
				val errorMessageObj = Gson().fromJson(message(), ErrorResponse::class.java)
				errorMessageObj.message?.replace("\"", "")
			} catch (e: Exception) {
				onError(e.message.toString())
				null
			}
			if (message != null) {
				onError(message)
			}
		}.onException {
			if (exception is IOException) {
				// Handle network-related errors
				onError(NO_CONNECTION)
			} else {
				// Handle other types of errors
				onError(UNEXPECTED_ERROR)
			}
		}
	}.flowOn(Dispatchers.IO)

}