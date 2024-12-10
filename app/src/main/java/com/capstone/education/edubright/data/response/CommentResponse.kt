package com.capstone.education.edubright.data.response

import com.google.gson.annotations.SerializedName

data class CommentResponse(

    @field:SerializedName("feedbackResult")
    val feedbackResult: FeedbackResult? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class FeedbackResult(

    @field:SerializedName("comment_text")
    val commentText: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("feedback_value")
    val feedbackValue: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
