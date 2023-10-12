package com.amora.movieku.data.model.network

import com.squareup.moshi.Json

data class MovieDetail(

	@Json(name="imdb_id")
	val imdb_id: String? = null,

	@Json(name="title")
	val title: String? = null,

	@Json(name="backdrop_path")
	val backdrop_path: String? = null,

	@Json(name="genres")
	val genres: List<Genres?>? = null,

	@Json(name="popularity")
	val popularity: Double? = null,

	@Json(name="id")
	val id: Long? = null,

	@Json(name="vote_count")
	val vote_count: Int? = null,

	@Json(name="budget")
	val budget: Int? = null,

	@Json(name="overview")
	val overview: String? = null,

	@Json(name="original_title")
	val original_title: String? = null,

	@Json(name="runtime")
	val runtime: Int? = null,

	@Json(name="poster_path")
	val poster_path: String? = null,

	@Json(name="release_date")
	val release_date: String? = null,

	@Json(name="voteAverage")
	val vote_average: Double? = null,

	@Json(name="tagline")
	val tagline: String? = null,

	@Json(name="adult")
	val adult: Boolean? = null,

	@Json(name="homepage")
	val homepage: String? = null,

	@Json(name="status")
	val status: String? = null
)
