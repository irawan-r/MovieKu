package com.amora.movieku.data.model.network

import com.squareup.moshi.Json

data class MovieVideoResponse(

	@Json(name="page")
	val page: Int,

	@Json(name="totalPages")
	val totalPages: Int,

	@Json(name="results")
	val results: List<VideoMovie>,

	@Json(name="totalResult")
	val totalResult: Int
)