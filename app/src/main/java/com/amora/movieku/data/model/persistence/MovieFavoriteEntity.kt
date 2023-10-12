package com.amora.movieku.data.model.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amora.movieku.data.model.network.Movie
import com.amora.movieku.data.model.network.Movie.Companion.toMovie
import com.amora.movieku.data.model.persistence.MovieFavoriteEntity.Companion.toMovie

@Entity(tableName = "movie_favorite")
data class MovieFavoriteEntity(
	val overview: String? = null,

	val title: String? = null,

	@ColumnInfo(name = "poster_path")
	val posterPath: String? = null,

	@ColumnInfo(name = "release_date")
	val releaseDate: String? = null,

	val popularity: Double? = null,

	@ColumnInfo(name = "voteAverage")
	val voteAverage: Int? = null,

	@PrimaryKey(autoGenerate = true)
	val id: Long = 0L,

	val remoteId: Long,

	@ColumnInfo(name = "vote_count")
	val voteCount: Int? = null
) {
	companion object {
		fun List<MovieFavoriteEntity>.toMovie(): List<Movie> {
			return this.map { it.toMovie() }
		}
	}
}
