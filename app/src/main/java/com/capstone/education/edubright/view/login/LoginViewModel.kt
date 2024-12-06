package com.capstone.education.edubright.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.education.edubright.data.UserRepository
import com.capstone.education.edubright.data.pref.Result
import com.capstone.education.edubright.data.pref.UserModel
import com.capstone.education.edubright.data.response.LoginRequest
import com.capstone.education.edubright.data.response.LoginResult
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _loginResult = MutableLiveData<Result<LoginResult?>>()
    val loginResult: LiveData<Result<LoginResult?>> = _loginResult

    fun loginUser(loginRequest: LoginRequest) {
        viewModelScope.launch {
            repository.loginUser(loginRequest).collect {
                _loginResult.value = it
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}