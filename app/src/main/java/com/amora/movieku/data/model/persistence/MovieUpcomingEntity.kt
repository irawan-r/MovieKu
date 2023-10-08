package com.amora.movieku.data.model.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_upcoming")
data class MovieUpcomingEntity(

	val overview: String? = null,

	val title: String? = null,

	val posterPath: String? = null,

	val releaseDate: String? = null,

	val popularity: Double? = null,

	val voteAverage: Int? = null,

	@PrimaryKey
	val id: Long,

	val voteCount: Int? = null
)