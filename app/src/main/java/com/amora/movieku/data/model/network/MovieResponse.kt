package com.amora.movieku.data.model.network

import com.amora.movieku.data.model.network.Movie.Companion.toPopularEntity
import com.amora.movieku.data.model.network.Movie.Companion.toFavoriteEntity
import com.amora.movieku.data.model.persistence.MovieFavoriteEntity
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import com.squareup.moshi.Json

data class MovieResponse(

	@Json(name="page")
	val page: Int,

	@Json(name="total_pages")
	val total_pages: Int,

	@Json(name="results")
	val results: List<Movie>,

	@Json(name="total_result")
	val total_result: Int
) {
	companion object {
		fun List<Movie>.toPopularEntity(): List<MoviePopularEntity> {
			return this.map { it.toPopularEntity() }
		}

		fun List<Movie>.toFavoriteEntity(): List<MovieFavoriteEntity> {
			return this.map { it.toFavoriteEntity() }
		}
	}
}
