package com.capstone.education.edubright.data.retrofit

import com.capstone.education.edubright.data.response.CommentResponse
import com.capstone.education.edubright.data.response.FeedbackStatisticsResponse
import com.capstone.education.edubright.data.response.LoginResponse
import com.capstone.education.edubright.data.response.PredictRequest
import com.capstone.education.edubright.data.response.PredictResponse
import com.capstone.education.edubright.data.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("/predict")
    fun predict(
        @Body request: PredictRequest): Call<PredictResponse>

    // New or updated endpoints
    @GET("feedback-statistics")
    suspend fun getFeedbackStatistics(): FeedbackStatisticsResponse

    @FormUrlEncoded
    @POST("comment")
    suspend fun postComment(
        @Field("user_id") userId: String,
        @Field("comment_text") commentText: String,
        @Field("feedback_value") feedbackValue: String
    ): CommentResponse
}