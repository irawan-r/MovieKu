package com.amora.movieku.network

import com.amora.movieku.data.model.network.MovieDetail
import com.amora.movieku.data.model.network.MovieResponse
import com.amora.movieku.data.model.network.MovieVideoResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
	@GET("movie/popular")
	suspend fun getPopularMovies(
		@Query("page") page: Int?
	): MovieResponse

	@GET("movie/upcoming")
	suspend fun getMovieUpcoming(
		@Query("page") page: Int?
	): MovieResponse

	@GET("search/movie")
	suspend fun searchMovies(
		@Query("query") keywords: String?
	): ApiResponse<MovieResponse>

	@GET("movie/{id}")
	suspend fun getMovieById(
		@Path("id") id: Long
	): ApiResponse<MovieDetail>

	@GET("movie/{id}/videos")
	suspend fun getMovieVideos(
		@Path("id") id: Long
	): ApiResponse<MovieVideoResponse>
}