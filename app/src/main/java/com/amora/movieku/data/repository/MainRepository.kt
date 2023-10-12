package com.amora.movieku.data.repository

import androidx.paging.PagingData
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.network.MovieDetail
import com.amora.movieku.data.model.network.MovieResponse
import com.amora.movieku.data.model.network.MovieReviewsResponse
import com.amora.movieku.data.model.network.MovieVideoResponse
import com.amora.movieku.data.model.persistence.MovieFavoriteEntity
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import kotlinx.coroutines.flow.Flow

interface MainRepository {
	fun getPopularMovies(): Flow<PagingData<MoviePopularEntity>>

	fun getFavoriteMovies(): Flow<List<MovieFavoriteEntity>>

	suspend fun insertFavoriteMovie(movie: Movie)
	suspend fun deleteFavoriteMovie(movie: Movie)

	suspend fun getFavoriteMovie(idMovie: Long): Movie?

	fun searchMovies(keywords: String?,
	                 onSuccess: (MovieResponse) -> Unit,
	                 onError: (String) -> Unit
	): Flow<MovieResponse>

	fun movieDetail(
		idMovie: Long, onSuccess: (MovieDetail) -> Unit,
		onError: (String) -> Unit
	): Flow<MovieDetail>

	fun movieVideo(
		idMovie: Long, onSuccess: (MovieVideoResponse) -> Unit,
		onError: (String) -> Unit
	): Flow<MovieVideoResponse>

	fun movieReviews(
		idMovie: Long, onSuccess: (MovieReviewsResponse) -> Unit,
		onError: (String) -> Unit
	): Flow<MovieReviewsResponse>
}