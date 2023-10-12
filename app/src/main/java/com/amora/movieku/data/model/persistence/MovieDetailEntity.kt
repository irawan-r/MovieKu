package com.amora.movieku.data.model.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amora.movieku.data.model.network.Genres
import com.squareup.moshi.Json

@Entity(tableName = "movie_detail")
data class MovieDetailEntity(

	@Json(name="imdb_id")
	val imdbId: String? = null,

	@Json(name="title")
	val title: String? = null,

	@Json(name="backdrop_path")
	val backdropPath: String? = null,

	@Json(name="genres")
	val genres: List<Genres?>? = null,

	@Json(name="popularity")
	val popularity: Double? = null,

	@PrimaryKey
	@Json(name="id")
	val id: Int,

	@Json(name="vote_count")
	val voteCount: Int? = null,

	@Json(name="budget")
	val budget: Int? = null,

	@Json(name="overview")
	val overview: String? = null,

	@Json(name="original_title")
	val originalTitle: String? = null,

	@Json(name="runtime")
	val runtime: Int? = null,

	@Json(name="poster_path")
	val posterPath: String? = null,

	@Json(name="release_date")
	val releaseDate: String? = null,

	@Json(name="voteAverage")
	val voteAverage: Int? = null,

	@Json(name="tagline")
	val tagline: String? = null,

	@Json(name="adult")
	val adult: Boolean? = null,

	@Json(name="homepage")
	val homepage: String? = null,

	@Json(name="status")
	val status: String? = null
)
