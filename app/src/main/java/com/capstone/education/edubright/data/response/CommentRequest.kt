package com.capstone.education.edubright.data.response

import com.google.gson.annotations.SerializedName

data class CommentRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("text") val commentText: String
)
