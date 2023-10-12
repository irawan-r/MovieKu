package com.amora.movieku.data.model.network

import com.amora.movieku.data.model.persistence.MovieFavoriteEntity
import com.amora.movieku.data.model.persistence.MoviePopularEntity
import com.amora.movieku.data.model.persistence.MovieUpcomingEntity
import com.squareup.moshi.Json

data class Movie(
	@field:Json(name="overview")
	val overview: String? = null,

	@field:Json(name="title")
	val title: String? = null,

	@field:Json(name="poster_path")
	val poster_path: String?,

	@field:Json(name="release_date")
	val releaseDate: String?,

	@field:Json(name="popularity")
	val popularity: Double?,

	@field:Json(name="vote_average")
	val voteAverage: Int?,

	@field:Json(name="id")
	val id: Long,

	@field:Json(name="vote_count")
	val vote_count: Int? = null
) {
	companion object {

		fun MoviePopularEntity.toMovie(): Movie {
			return Movie(
				overview = overview,
				title = title,
				popularity = popularity,
				voteAverage = voteAverage,
				id = remoteId,
				vote_count = voteCount,
				poster_path = posterPath,
				releaseDate = releaseDate
			)
		}

		fun MovieUpcomingEntity.toMovie(): Movie {
			return Movie(
				overview = overview,
				title = title,
				popularity = popularity,
				voteAverage = voteAverage,
				id = remoteId,
				vote_count = voteCount,
				poster_path = posterPath,
				releaseDate = releaseDate
			)
		}
		fun Movie.toPopularEntity(): MoviePopularEntity {
			return MoviePopularEntity(
                overview = overview,
                title = title,
                posterPath = poster_path,
                releaseDate = releaseDate,
                popularity = popularity,
                voteAverage = voteAverage,
				remoteId = id,
                voteCount = vote_count
            )
		}

		fun Movie.toFavoriteEntity(): MovieFavoriteEntity {
			return MovieFavoriteEntity(
				overview = overview,
				title = title,
				posterPath = poster_path,
				releaseDate = releaseDate,
				popularity = popularity,
				voteAverage = voteAverage,
				remoteId = id,
				voteCount = vote_count,
				id = id
			)
		}

		fun MovieFavoriteEntity.toMovie(): Movie {
			return Movie(
				overview = overview,
				title = title,
				popularity = popularity,
				voteAverage = voteAverage,
				id = remoteId,
				vote_count = voteCount,
				poster_path = posterPath,
				releaseDate = releaseDate
			)
		}
	}
}
