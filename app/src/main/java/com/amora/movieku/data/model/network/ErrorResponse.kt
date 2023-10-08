package com.amora.movieku.data.model.network

import com.squareup.moshi.Json

data class ErrorResponse(

	@Json(name="error")
	val error: Boolean? = null,

	@Json(name="message")
	val message: String? = null
)
