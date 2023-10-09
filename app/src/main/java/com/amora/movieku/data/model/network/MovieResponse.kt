package com.amora.movieku.data.model.network

import com.amora.movieku.data.model.network.Movie.Companion.toPopularEntity
import com.amora.movieku.data.model.network.Movie.Companion.toUpcomingEntity
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import com.amora.movieku.data.model.persistence.MovieUpcomingEntity
import com.squareup.moshi.Json

data class MovieResponse(

	@Json(name="page")
	val page: Int,

	@Json(name="total_pages")
	val total_pages: Int,

	@Json(name="results")
	val results: List<Movie>,

	@Json(name="totalResult")
	val totalResult: Int
) {
	companion object {
		fun List<Movie>.toPopularEntity(): List<MoviePopularEntity> {
			return this.map { it.toPopularEntity() }
		}

		fun List<Movie>.toUpcomingEntity(): List<MovieUpcomingEntity> {
			return this.map { it.toUpcomingEntity() }
		}
	}
}
