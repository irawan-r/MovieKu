package com.amora.movieku.utils

import androidx.room.TypeConverter
import com.amora.movieku.data.model.network.Genres
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
	private val moshi = Moshi.Builder().build()

	@TypeConverter
	fun fromGenresList(genres: List<Genres>?): String? {
		if (genres == null) return null
		val type = Types.newParameterizedType(List::class.java, Genres::class.java)
		val adapter = moshi.adapter<List<Genres>>(type)
		return adapter.toJson(genres)
	}

	@TypeConverter
	fun toGenresList(genresString: String?): List<Genres>? {
		if (genresString == null) return null
		val type = Types.newParameterizedType(List::class.java, Genres::class.java)
		val adapter = moshi.adapter<List<Genres>>(type)
		return adapter.fromJson(genresString)
	}
}