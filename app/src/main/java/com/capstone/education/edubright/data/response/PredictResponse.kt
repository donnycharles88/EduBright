package com.capstone.education.edubright.data.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(
	val success: Boolean,
	val prediction: String?,
	val timestamp: String,
	val message: String
)
