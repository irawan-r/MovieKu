package com.amora.movieku.data.model.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


@Entity("video")
data class VideoMovie(

	@Json(name="site")
	val site: String? = null,

	@Json(name="name")
	val name: String? = null,

	@Json(name="official")
	val official: Boolean? = null,

	@Json(name="id")
	val id: String? = null,

	@Json(name="type")
	val type: String? = null,

	@Json(name="published_at")
	val publishedAt: String? = null,

	@Json(name="key")
	val key: String? = null
)
