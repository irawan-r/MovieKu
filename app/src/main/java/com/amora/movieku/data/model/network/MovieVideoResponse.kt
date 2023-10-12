package com.amora.movieku.data.model.network

import com.squareup.moshi.Json

data class MovieVideoResponse(

	@Json(name="page")
	val page: Int,

	@Json(name="total_pages")
	val totalPages: Int,

	@Json(name="results")
	val results: List<VideoMovie>,

	@Json(name="total_result")
	val totalResult: Int
)