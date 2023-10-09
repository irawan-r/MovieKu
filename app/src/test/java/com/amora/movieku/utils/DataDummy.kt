package com.amora.movieku.utils

import com.amora.movieku.BuildConfig
import com.amora.movieku.data.model.network.Movie

object DataDummy {

	fun getToken() =
		"Bearer ${BuildConfig.API_KEY}"

	fun generateDataDummy(): List<Movie> {
		val newList = mutableListOf<Movie>()

		for (i in 0..9) {
			val story = Movie(
				id = 1902380192L+i,
				overview = "kasudhfkjasd",
				title = "Test$i",
				poster_path = "asdfjkh",
				releaseDate = "sakdjfhas",
				popularity = 2387.09,
				vote_count = 129837,
				voteAverage = 28912
			)
			newList.add(story)
		}
		return newList
	}

	fun generateEmptyStories(): List<Movie> {
		return emptyList()
	}
}