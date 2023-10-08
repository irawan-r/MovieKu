package com.amora.movieku.data.model.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


@Entity(tableName = "video")
data class VideoMovieEntity(

	val site: String? = null,

	val name: String? = null,

	val official: Boolean? = null,

	@PrimaryKey
	val id: String,

	val type: String? = null,

	val publishedAt: String? = null,

	val key: String? = null
)
