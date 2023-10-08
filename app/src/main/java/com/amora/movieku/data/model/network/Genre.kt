package com.amora.movieku.data.model.network

import com.squareup.moshi.Json

data class Genres(
	@Json(name="name")
	val name: String? = null
)
