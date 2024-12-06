package com.capstone.education.edubright.di

import android.content.Context
import com.capstone.education.edubright.data.UserRepository
import com.capstone.education.edubright.data.pref.UserPreference
import com.capstone.education.edubright.data.retrofit.ApiConfig
import com.capstone.education.edubright.data.room.SentimentDatabase

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context)
        val sentimentDao = SentimentDatabase.getInstance(context).sentimentDao() // Tambahkan inisialisasi SentimentDao
        return UserRepository.getInstance(userPreference, apiService, sentimentDao) // Tambahkan sentimentDao
    }
}
