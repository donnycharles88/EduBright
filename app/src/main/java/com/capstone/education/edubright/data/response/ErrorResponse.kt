package com.capstone.education.edubright.data.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

	@field:SerializedName("details")
	val details: String,

	@field:SerializedName("error")
	val error: String,

	@field:SerializedName("status")
	val status: String
)
