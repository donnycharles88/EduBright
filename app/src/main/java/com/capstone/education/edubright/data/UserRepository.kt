package com.capstone.education.edubright.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.capstone.education.edubright.data.pref.Result
import com.capstone.education.edubright.data.pref.UserModel
import com.capstone.education.edubright.data.pref.UserPreference
import com.capstone.education.edubright.data.response.LoginRequest
import com.capstone.education.edubright.data.response.LoginResponse
import com.capstone.education.edubright.data.response.LoginResult
import com.capstone.education.edubright.data.response.PredictRequest
import com.capstone.education.edubright.data.response.PredictResponse
import com.capstone.education.edubright.data.response.RegisterResponse
import com.capstone.education.edubright.data.retrofit.ApiService
import com.capstone.education.edubright.data.room.Sentiment
import com.capstone.education.edubright.data.room.SentimentDao
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val sentimentDao: SentimentDao
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val _registerUser = MutableLiveData<RegisterResponse>()
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: MutableLiveData<String?> = _successMessage
    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: MutableLiveData<String?> = _errorLiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val sentimentData = MutableLiveData<Map<String, Int>>()

    init {
        loadSentimentData()
    }
    private fun loadSentimentData() {
        repositoryScope.launch {
            val sentiments = sentimentDao.getAllSentiments()
            val sentimentMap = sentiments.associate { it.label to it.count }
            sentimentData.postValue(sentimentMap)
        }
    }

    fun getSentimentData(): LiveData<Map<String, Int>> = sentimentData

    fun updateSentiments(prediction: String) {
        repositoryScope.launch {
            val currentData = sentimentData.value?.toMutableMap() ?: mutableMapOf()
            currentData[prediction] = (currentData[prediction] ?: 0) + 1

            val sentiment = Sentiment(label = prediction, count = currentData[prediction] ?: 0)
            insertOrUpdateSentiment(sentiment)
            sentimentData.postValue(currentData)
        }
    }

    private suspend fun insertOrUpdateSentiment(sentiment: Sentiment) {
        val existingSentiment = sentimentDao.getSentimentByLabel(sentiment.label)
        if (existingSentiment != null) {
            sentimentDao.updateSentiment(sentiment)
        } else {
            sentimentDao.insert(sentiment)
        }
    }


    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _registerUser.value = response.body()
                    val successMessage = response.body()?.message
                    _successMessage.postValue(successMessage)


                } else {
                    val errorMessage = response.errorBody()?.string()
                    _errorLiveData.postValue(errorMessage)
                    Log.e("postRegister", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("postRegister", "onFailure: ${t.message}")
            }
        })
    }
    fun loginUser(loginRequest: LoginRequest): Flow<Result<LoginResult?>> = flow {
        emit(Result.Loading)
        try {
            val responseLogin = apiService.login(loginRequest.email, loginRequest.password)
            val result = responseLogin.loginResult
            emit(Result.Success(result))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(Result.Error(errorResponse.message ?: "Unknown error"))
        }
    }
    fun predictSentiment(feedback: String): Call<PredictResponse> {
        val request = PredictRequest(feedback)
        return apiService.predict(request)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            sentimentDao: SentimentDao
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService, sentimentDao)
            }.also { instance = it }
    }
}