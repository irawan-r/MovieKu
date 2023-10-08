package com.amora.movieku.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.amora.movieku.data.model.network.ErrorResponse
import com.amora.movieku.data.model.network.MovieDetail
import com.amora.movieku.data.model.network.MovieResponse
import com.amora.movieku.data.model.network.MovieReviewsResponse
import com.amora.movieku.data.model.network.MovieVideoResponse
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import com.amora.movieku.data.model.persistence.MovieUpcomingEntity
import com.amora.movieku.data.persistence.AppDatabase
import com.amora.movieku.network.ApiService
import com.amora.movieku.data.repository.remotemediator.popular.PopularRemoteMediator
import com.amora.movieku.data.repository.remotemediator.upcoming.UpcomingRemoteMediator
import com.google.gson.Gson
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
	private val apiService: ApiService,
	private val database: AppDatabase
) : MainRepository {
	@OptIn(ExperimentalPagingApi::class)
	override fun getPopularMovies(onError: (String) -> Unit): Flow<PagingData<MoviePopularEntity>> =
		flow {
			val pager = Pager(
				config = PagingConfig(pageSize = 10),
				remoteMediator = PopularRemoteMediator(database, apiService, 0),
				pagingSourceFactory = {
					database.movieDao().getMoviesPopularList()
				}
			).flow
				.catch {
					onError(it.message.toString())
				}
			emitAll(pager)
		}

	@OptIn(ExperimentalPagingApi::class)
	override fun getUpcomingMovies(): Flow<PagingData<MovieUpcomingEntity>> = flow {
		val pager = Pager(
			config = PagingConfig(pageSize = 10),
			remoteMediator = UpcomingRemoteMediator(database, apiService, 1),
			pagingSourceFactory = {
				database.movieDao().getMoviesUpcomingList()
			}
		)

		emitAll(pager.flow)
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
			onError(this.message.toString())
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
			onError(this.message.toString())
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
			onError(this.message.toString())
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
			onError(this.message.toString())
		}
	}.flowOn(Dispatchers.IO)

}