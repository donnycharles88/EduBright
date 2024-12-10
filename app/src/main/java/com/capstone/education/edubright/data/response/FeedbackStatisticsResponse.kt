package com.capstone.education.edubright.data.response

import com.google.gson.annotations.SerializedName

data class FeedbackStatisticsResponse(

    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("totalFeedbacks")
    val totalFeedbacks: Int,

    @SerializedName("statistics")
    val statistics: List<FeedbackStatistics>
)

data class FeedbackStatistics(

    @SerializedName("feedback")
    val feedback: String,

    @SerializedName("count")
    val count: Int,

    @SerializedName("percentage")
    val percentage: String
)
