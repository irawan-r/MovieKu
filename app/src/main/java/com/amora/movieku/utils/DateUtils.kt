package com.amora.movieku.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
	fun formatDate(inputDate: String?): String {
		val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

		val date = inputFormat.parse(inputDate)
		return outputFormat.format(date!!)
	}
}