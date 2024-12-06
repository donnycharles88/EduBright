package com.capstone.education.edubright.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("registerResult")
	val registerResult: RegisterResult,

	@field:SerializedName("status")
	val status: String
)

data class RegisterResult(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("email")
	val email: String
)
