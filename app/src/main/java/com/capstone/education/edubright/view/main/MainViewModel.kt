package com.capstone.education.edubright.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.education.edubright.data.UserRepository
import com.capstone.education.edubright.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    val isDarkMode: LiveData<Boolean> = repository.getThemePreference()
    fun toggleTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            repository.setThemePreference(isDarkMode)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}

